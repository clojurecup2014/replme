(ns replme.web.routes
  (:require [compojure.core :refer [routes ANY GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as resp]
            [replme.web.repl :refer [open-websocket]]))

(defn- main-routes
  [docker]
  (routes
   (GET "/repl" [:as req] (open-websocket (:client docker) req))
   (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
   (route/resources "/")
   (route/not-found "/404.html"))) 

(defn handler
  [docker]
  (handler/site (main-routes docker)))
