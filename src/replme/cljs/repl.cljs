p(ns replme.cljs.repl
  (:require [cljs.core.async :refer [<! >! sub chan]]
            [replme.cljs.websocket :as ws])
  (:use [jayq.core :only [$]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(def $repl-container ($ :#repl-container))

(enable-console-print!)

(defn handle-command
  [socket]
  (fn [input]
    (ws/socket-send socket input)
    (array (js-obj "msg" ""))))

(defn repl-config
  [in-chan]
  (js-obj "promptLabel" ">>"
          "autofocus" true
          "welcomeMessage" "Welcome! Type in some clojure to get started"
          "animateScroll" true
          "commandHandle" (handle-command in-chan)
          "promptHistory" true))

(defn init [socket socket-out-pub]
  (let [socket socket
        socket-out-sub (sub socket-out-pub :socket-out (chan))
        console (.console $repl-container (repl-config socket))]
    (go-loop [msg (<! socket-out-sub)]
      (.report console (array (js-obj "msg" msg)))
      (recur (<! socket-out-sub)))))
