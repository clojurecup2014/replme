(ns replme.components.docker
  (:require [com.stuartsierra.component :as component]
            [docker.core :as docker]
            [docker.container :as container]
            [clojure.tools.logging :as log]))

(defrecord Docker [url client]
  component/Lifecycle
  (start [component]
    (if (:client component) 
      component
      (do (log/info (str "Connecting to docker server at " url))
          (let [client (docker/make-client url)]
            (doseq [old-container (map :Id (container/show-all client))]
              (container/kill client old-container)
              (container/remove client old-container :force true))
            (assoc component :client client)))))

  (stop [component]
    (if-not (:client component)
      component
      (do (log/info "Disconnected from Docker")
          (dissoc component :client)))))

(defn new-docker
  [url]
  (map->Docker {:url url}))
