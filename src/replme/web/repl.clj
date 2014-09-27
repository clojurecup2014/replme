(ns replme.web.repl
  (:require [clojure.core.async :refer [go <! >! <!! >!! chan close! go-loop]]
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
        url (str "http://" (:host client) "/containers/" id "/logs?stdout=1&follow=1")
        http-client (http/create-client)]
    (go
      (log/info "Reading logs from: " url)
      (doseq [msg (http/string (http/stream-seq http-client :get url))]
        (log/info "Container" id "logs:" msg)
        (>! output msg)))
    [output http-client]))

(defn- docker-cmd
  []
  {:Image "edpaget/lein"
   :Tty true
   :Cmd ["/opt/lein" "repl" ":headless" ":host" "0.0.0.0" ":port" "8081"]})

(defn- start-docker
  [client]
  (let [container (container/create client (docker-cmd))
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
  (container/stop client id))

(defn- docker-repl
  [client in-chan out-chan]
  (let [[id [stdout http-client]] (start-docker client)
        ip (docker-ip client id)
        port 8081]
    (go-loop [msg (<! stdout)]
      (if (re-find nrepl-sentinel msg)
        (do
          (log/info (str "Connecting to docker nrepl at" ip ":" port))
          (>! out-chan "REPL OK")
          (go-loop [repl-conn (repl/connect :host ip :port port)
                    client (repl/client repl-conn 1000)
                    command (<! in-chan)]
            (->> (repl/message client {:op :eval :code command})
                 repl/response-values
                 prn-str
                 (>! out-chan)))
          (recur repl-conn (<! in-chan) client))
        (do (>! out-chan msg)
            (recur (<! stdout)))))
    [id http-client]))

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
    (http/close http-client)
    (doseq [c chans] (close! c))))

(defn- handle-out
  [channel out-chan]
  (go-loop [msg (<! out-chan)]
    (send! channel msg false)
    (when (open? channel)
      (recur (<! out-chan)))))

(defn open-websocket
  [docker-client req]
  (let [in-chan (chan)
        out-chan (chan)]
    (with-channel req channel
      (if (websocket? channel)
        (let [[docker-id http-client] (docker-repl docker-client in-chan out-chan)
              send-chan (handle-out channel out-chan)]
          (log/info "Websocket Connected")
          (on-receive channel (handle-input in-chan))
          (on-close channel (handle-close docker-client docker-id http-client in-chan out-chan send-chan)))
        (send! channel (resp-bad-request))))))
