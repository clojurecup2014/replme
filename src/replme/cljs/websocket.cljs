(ns replme.cljs.websocket
  (:require [cljs.core.async :refer [>! chan pub]]
            [cljs.reader :as reader])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn new-socket
  [url]
  (js/WebSocket. url))

(defn close-socket [socket]
  (.close socket 1000))

(defn socket-send
  [socket msg]
  (.send socket msg))

(defn format-output
  [message]
  (println (list? message))
  (cond
   (list? message) message
   (vector? message) (first message)
   (string? message) message
   :else "nil"))

(defn handle-fn
  [out-chan]
  (fn [msg]
    (go (>! out-chan (-> (.-data msg)
                         reader/read-string
                         (update-in [:message] format-output))))))

(defn socket-out-pub
  [socket]
  (let [out-chan (chan)]
    (set! (.-onmessage socket) (handle-fn out-chan))
    (pub out-chan (fn [{:keys [destination]}] (println destination) destination))))
