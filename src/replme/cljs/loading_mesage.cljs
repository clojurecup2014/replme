(ns replme.cljs.loading-message
  (:require [cljs.core.async :refer [<! >! chan sub]]
            [replme.cljs.websocket :as ws])
  (:use [jayq.core :only [$]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

(def $loading-message ($ :#loading-message))

(defn init [socket-out-pub]
  (let [socket-out-sub (sub socket-out-pub :socket-out (chan))]
    (go-loop [msg (<! socket-out-sub)]
             (.log js/console msg)
             (recur (<! socket-out-sub)))))
