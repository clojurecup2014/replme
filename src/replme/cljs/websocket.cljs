(ns replme.cljs.websocket
  (:require [cljs.core.async :refer [>! chan pub]]
            [cljs.reader :as reader])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn new-socket
  [url]
  (js/WebSocket. url))

(defn socket-send
  [socket msg]
  (.send socket msg))

(defn handle-fn
  [out-chan]
  (fn [msg]
    (go (if-let [val (->> (.-data msg)
                          reader/read-string
                          first)]
          (>! out-chan val)
          (>! out-chan "nil")))))

(defn socket-out-pub
  [socket]
  (let [out-chan (chan)]
    (set! (.-onmessage socket) (handle-fn out-chan))
    (pub out-chan (fn [] :socket-out))))
