(ns replme.cljs.main
  (:use [jayq.core :only [document-ready]])
  (:require [replme.cljs.repo-input :as repo-input]
            [replme.cljs.repl :as repl]
            [replme.cljs.loading-message :as loading-message]
            [replme.cljs.websocket :as ws]
            [replme.cljs.about-page :as about-page]
            [cljs.core.async :refer [<! chan close!]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn base-url
  []
  (str "ws://" (.-host js/location) "/repl"))

(defn socket-url
  []
  (let [url-path (apply str (rest (.-pathname js/location)))]
    (if (empty? url-path)
      (base-url)
      (str (base-url) "?repo=" url-path))))

(defn initialize-app []
  (let [control-chan (chan)]
    (go-loop [url (socket-url)]
      (let [socket (ws/new-socket url)
            socket-out (ws/socket-out-pub socket)]
        (repl/init socket socket-out)
        (loading-message/init socket-out)
        (repo-input/init control-chan)
        (about-page/init)
        (when-let [new-repo (<! control-chan)]
          (ws/close-socket socket)
          (.pushState js/history (js-obj "state" 1) "next repo" (str "/" new-repo))
          (recur (str (base-url) "?repo=" new-repo)))))))

(document-ready initialize-app)
