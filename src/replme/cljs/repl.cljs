(ns replme.cljs.repl
  (:require [cljs.core.async :refer [<! >! sub chan]]
            [replme.cljs.websocket :as ws])
  (:use [jayq.core :only [$]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

(defn handle-command
  [socket]
  (fn [input]
    (ws/socket-send socket input)
    (array (js-obj "msg" ""))))

(defn repl-config
  [socket]
  (js-obj "promptLabel" "user>"
          "autofocus" true
          "welcomeMessage" "Welcome! Type in some clojure to get started"
          "animateScroll" true
          "commandHandle" (handle-command socket)
          "promptHistory" true))

(defn format-msg
  [{:keys [error value out]}]
  (if-not (empty? error)
    (str "Threw " (:ex error) (:root-ex error) "message:\n" (:err error))
    (str out value)))

(defn init [socket socket-out-pub]
  (let [socket socket
        socket-out-sub (sub socket-out-pub :repl (chan 1 (map :message)))
        $repl-container ($ :#repl-container)
        console (.console $repl-container (repl-config socket))]
    (go-loop [{:keys [namespace] :as msg} (<! socket-out-sub)]
      (println msg)
      (.report console (array (js-obj "msg" (format-msg msg))))
      (recur (<! socket-out-sub)))))
