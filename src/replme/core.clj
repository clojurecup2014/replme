(ns replme.core
  (:require [com.stuartsierra.component :as component]
            [replme.components.docker :refer [new-docker]]
            [replme.components.app :refer [new-app]]
            [replme.web.routes :as routes])
  (:gen-class true :main))

(defn- system
  [{:keys [port docker-url]
    :or {port 8080 docker-url "unix:///var/run/docker.sock"}}]
  (component/system-map
   :docker (new-docker docker-url)
   :app (component/using (new-app port routes/handler) [:docker])))

(defn -main
  [& [port docker-url]]
  (let [sys (system {:port (Integer/parseInt port) :docker-url docker-url} )]
    (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (component/stop sys))))
    (component/start sys)))
