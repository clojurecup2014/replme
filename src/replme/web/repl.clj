(ns replme.web.repl
  (:require [clojure.core.async :refer [go <! <!! map< sub chan close! go-loop >! timeout alts!]]
            [org.httpkit.server :refer [send! with-channel on-receive on-close open? websocket?]]
            [clojure.tools.logging :as log]
            [replme.web.resp :refer :all]
            [clojure.tools.nrepl :as repl]
            [docker.container :as container]))

(defn- start-docker
  [client]
  (let [container (container/create client {:Image "edpaget/lein"})
        id (:Id container)] 
    (container/start client id)
    id))

(defn- docker-ip
  [client id]
  (-> (container/inspect client id)
      :NetworkSettings
      :IPAddress))

(defn- stop-docker
  [client id]
  (container/stop client id))

(defn- repl-connect
  [ip])

(defn- docker-repl
  [client in-chan out-chan]
  (let [id (start-docker client)
        ip (docker-ip client id)
        port 8181]
    (with-open [conn (repl/connect :host ip :port port)]
      (let [client (repl/client conn 1000)]
        (go-loop [command (<! in-chan)]
          (repl/message client {:op :eval :code command})
          (>! out-chan (repl/response-values client))
          (recur (<! in-chan)))))
    id))

(defn- handle-input
  [in-chan]
  (fn [data]
    (<!! in-chan data)))

(defn- handle-close
  [client id & chans]
  (stop-docker client id)
  (doseq [c chans] (close! c)))

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
