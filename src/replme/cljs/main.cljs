(ns replme.cljs.main
  (:use [jayq.core :only [document-ready]])
  (:require [replme.cljs.repo-input :as repo-input]
            [replme.cljs.repl :as repl]
            [replme.cljs.loading-message :as loading-message]
            [replme.cljs.websocket :as ws]
            [replme.cljs.about-page :as about-page]))

(defn initialize-app []
  (let [url-base (.-host js/location)
        socket (ws/new-socket (str "ws://" url-base "/repl"))
        socket-out (ws/socket-out-pub socket)]
    (repl/init socket socket-out)
    (loading-message/init socket-out)
    (repo-input/init)
    (about-page/init)))

(document-ready initialize-app)
