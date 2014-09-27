(ns replme.components.docker
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]))

(defrecord Docker [url connection]
  component/Lifecycle
  (start [component]
    component)

  (stop [component]
    component))

(defn new-docker
  [url]
  (map->Docker {:url url}))
