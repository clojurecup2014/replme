(ns replme.web.repl
  (:require [clojure.core.async :refer [go <! >! <!! >!! chan close! go-loop alts! timeout]]
            [org.httpkit.server :refer [send! with-channel on-receive on-close open? websocket?]]
            [http.async.client :as http]
            [clojure.tools.logging :as log]
            [replme.web.resp :refer :all]
            [clojure.tools.nrepl :as repl]
            [docker.container :as container]))

(def nrepl-sentinel #"server started")

(defn- docker-attach
  [client id]
  (let [output (chan)
        url (str "http://" (:host client) "/containers/" id "/logs?stdout=1&follow=1")]
    (go
      (log/info "Reading logs from: " url)
      (with-open  [http-client (http/create-client)]
        (doseq [msg (http/string (http/stream-seq http-client :get url))]
          (log/info "Container" id "logs:" msg)
          (>! output msg))))
    output))

(defn- docker-cmd
  [args]
  (let [args (if args (vector args) [])]
    (log/info "Starting container from repo" args)
    {:Image "edpaget/lein"
     :Tty true
     :Memory "256M"
     :Cmd args}))

(defn- start-docker
  [client args]
  (let [container (container/create client (docker-cmd args))
        id (:Id container)
        logs (docker-attach client id)]
    (container/start client id)
    (log/info (str "Started container:" id))
    [id logs]))

(defn- docker-ip
  [client id]
  (-> (container/inspect client id)
      :NetworkSettings
      :IPAddress))

(defn- stop-docker
  [client id]
  (container/kill client id)
  (container/remove client id :force true))

(defn- out-msg
  [msg destination]
  {:message msg :destination destination})

(defn- timeout-stdout
  [stdout]
  (go (or (first (alts! [stdout (timeout 30000)] :priority true)) "server started")))

(defn- docker-repl
  [client args in-chan out-chan]
  (let [[id stdout] (start-docker client args)
        ip (docker-ip client id)
        port 8081]
    (go-loop [msg (<! (timeout-stdout stdout))]
      (log/info msg)
      (if (re-find nrepl-sentinel msg)
        (do
          (log/info (str "Connecting to docker nrepl at" ip ":" port))
          (>! out-chan (out-msg "REPL OK" :command))
          (go-loop [command (<! in-chan)]
            (when command
              (with-open [repl-conn (repl/connect :host ip :port port)]
                (>! out-chan
                    (-> (repl/client repl-conn 1000)
                        (repl/message {:op :eval :code command})
                        (out-msg :repl)))))
            (recur (<! in-chan))))
        
        (do (>! out-chan (out-msg msg :console))
            (recur (<! stdout)))))
    id))

(defn- handle-input
  [in-chan]
  (fn [data]
    (log/info (str "Received:" data))
    (>!! in-chan data)))

(defn- handle-close
  [client id http-client & chans]
  (fn [_]
    (log/info "Closing Repl Connnection")
    (stop-docker client id)
    (doseq [c chans] (close! c))
    (log/info "Connection Closed!")))

(defn- handle-out
  [channel out-chan]
  (go-loop [msg (<! out-chan)]
    (log/info "Sending" msg)
    (send! channel msg false)
    (when (open? channel)
      (recur (<! out-chan)))))

(defn- not-empty?
  [{:keys [message]}]
  (cond
   (= "\n" message) false
   (= "\r\n" message) false
   (= "" message) false
   :else true))

(defn- msg-hash
  []
  {:error {} :out "" :value nil :namespace nil})

(defn formatter
  [out-hash in-hash]
  (reduce (fn [out-hash [k v]]
            (cond 
             (some #{k} [:ex :root-ex :err]) (update-in out-hash [:error] assoc k v)
             (= k :out) (update-in out-hash [:out] str v)
             (= k :ns) (assoc out-hash :namespace v)
             (= k :value) (assoc out-hash :value v)
             :else out-hash))
          out-hash in-hash))

(defn- format-msgs
  [{:keys [message] :as msg}]
  (cond
   (string? message) msg
   (seq? message) (assoc msg :message (reduce formatter (msg-hash) message))))

(def format-out (comp (filter not-empty?) (map format-msgs) (map pr-str)))

(defn open-websocket
  [docker-client req repo]
  (let [in-chan (chan)
        out-chan (chan 1 format-out)]
    (with-channel req channel
      (if (websocket? channel)
        (let [[docker-id http-client] (docker-repl docker-client repo in-chan out-chan)
              send-chan (handle-out channel out-chan)]
          (log/info "Websocket Connected")
          (on-receive channel (handle-input in-chan))
          (on-close channel (handle-close docker-client docker-id http-client in-chan out-chan send-chan)))
        (send! channel (resp-bad-request))))))
