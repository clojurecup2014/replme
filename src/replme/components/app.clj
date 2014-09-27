(ns replme.components.app
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [org.httpkit.server :refer [run-server]]))

(defrecord App [handler port docker server]
  component/Lifecycle
  (start [component]
    (if server
      component
      (let [s (run-server (handler docker)
                          {:port port :join? false})]
        (log/info (str "Starting Server on port " port))
        (assoc component :server s))))
  
  (stop [component]
    (if-not server
      component
      (do (server :timeout 100)
          (log/info (str "Stopping Server on port " port))
          (assoc component server nil)))))

(defn new-app
  [port handler]
  (map->App {:port port :handler handler}))
