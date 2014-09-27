(ns replme.web.repl
  (:require [clojure.core.async :refer [go <! >! <!! >!! chan close! go-loop]]
            [org.httpkit.server :refer [send! with-channel on-receive on-close open? websocket?]]
            [clojure.tools.logging :as log]
            [replme.web.resp :refer :all]
            [clojure.tools.nrepl :as repl]
            [docker.container :as container]))

(defn try-times*
  [n sleep thunk]
  (loop [n n]
    (if-let [result (try
                      [(thunk)]
                      (catch Exception e
                        (if (zero? n)
                          (throw e)
                          (Thread/sleep sleep))))]
      (result 0)
      (recur (dec n)))))

(defmacro try-times-with-sleep
  [n sleep & body]
  `(try-times* ~n ~sleep (fn [] ~@body)))

(defn- docker-attach
  [client id]
  (let [output (chan)
        buffer (container/attach client id :logs true :stdout true)]
    buffer))

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
    (log/info (str "Started container: " id))
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
  (let [[id stdout] (start-docker client)
        ip (docker-ip client id)
        port 8081
        repl-conn (try-times-with-sleep 10 1000
                                        (repl/connect :host ip :port port))]
    (log/info (str "Connecting to docker nrepl at " ip ":" port))
    (go-loop [command (<! in-chan)
              client (repl/client repl-conn 1000)]
      (->> (repl/message client {:op :eval :code command})
           repl/response-values
           prn-str
           (>! out-chan))
      (recur (<! in-chan) client))
    id))

(defn- handle-input
  [in-chan]
  (fn [data]
    (log/info (str "Received: " data))
    (>!! in-chan data)))

(defn- handle-close
  [client id & chans]
  (fn [_]
    (log/info "Closing Repl Connnection")
    (stop-docker client id)
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
        (let [docker-id (docker-repl docker-client in-chan out-chan)
              send-chan (handle-out channel out-chan)]
          (log/info "Websocket Connected")
          (on-receive channel (handle-input in-chan))
          (on-close channel (handle-close docker-client docker-id in-chan out-chan send-chan)))
        (send! channel (resp-bad-request))))))
