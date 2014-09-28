(ns replme.cljs.main
  (:use [jayq.core :only [document-ready]])
  (:require [replme.cljs.repo-input :as repo-input]
            [replme.cljs.repl :as repl]
            [replme.cljs.loading-message :as loading-message]
            [replme.cljs.websocket :as ws]
            [replme.cljs.about-page :as about-page]
            [replme.cljs.readme-section :as readme]
            [cljs.core.async :refer [<! chan close!]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn base-url
  []
  (str "ws://" (.-host js/location) "/repl"))

(defn socket-url
  [repo]
  (if (empty? repo)
    (base-url)
    (str (base-url) "?repo=" repo)))

(defn starting-repo
  []
  (apply str (rest (.-pathname js/location))))

(defn set-url
  [repo]
  (when repo
    (.pushState js/history nil nil (str "/" repo))))

(defn start-app [app-state comm-chan]
  (go-loop [{:keys [repo socket] :as state} app-state]
    (when socket
      (ws/close-socket socket))
    (when repo
      (set-url repo))
    (let [state (assoc state :socket (ws/new-socket (socket-url repo)))
          state (assoc state :socket-pub (ws/socket-out-pub (:socket state)))
          {:keys [socket socket-pub]} state]
      (repl/init socket socket-pub)
      (loading-message/init socket-pub)
      (repo-input/init comm-chan)
      (readme/init comm-chan state)
      (about-page/init)
      (recur (assoc state :repo (<! comm-chan))))))

(defn app-loop
  []
  (start-app {:repo (starting-repo)} (chan)))

(document-ready app-loop)
