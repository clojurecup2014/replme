(ns replme.components.docker
  (:require [com.stuartsierra.component :as component]
            [docker.core :as docker]
            [clojure.tools.logging :as log]))

(defrecord Docker [url client]
  component/Lifecycle
  (start [component]
    (if (:client component) 
      component
      (do (log/info (str "Connecting to docker server at " url))
          (assoc component :client (docker/make-client url)))))

  (stop [component]
    (if-not (:client component)
      component
      (do (log/info "Disconnected from Docker")
          (dissoc component :client)))))

(defn new-docker
  [url]
  (map->Docker {:url url}))
