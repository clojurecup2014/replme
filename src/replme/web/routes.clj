(ns replme.web.routes
  (:require [compojure.core :refer [routes ANY]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [liberator.core :refer [resource]]))

(defn- main-routes
  []
  (routes
   (route/resources "/")
   (route/not-found "/404.html"))) 

(defn handler
  []
  (handler/site (main-routes)))
