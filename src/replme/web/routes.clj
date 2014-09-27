(ns replme.web.routes
  (:require [compojure.core :refer [routes ANY GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :as resp]
            [liberator.core :refer [resource]]))

(defn- main-routes
  []
  (routes
   (GET "/" [] (resp/resource-response "index.html" {:root "public"}))
   (route/resources "/")
   (route/not-found "/404.html"))) 

(defn handler
  []
  (handler/site (main-routes)))
