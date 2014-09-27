(ns replme.cljs.loading-message
  (:require [cljs.core.async :refer [<! >! chan sub]]
            [replme.cljs.websocket :as ws])
  (:use [jayq.core :only [$ fade-out append prop anim height]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

(defn scroll-to-bottom [$container]
  (let [scroll-height (prop $container "scrollHeight")]
    (anim $container {:scrollTop scroll-height} 100)))

(defn append-text-to-container [$container text]
  (append $container (str text))
  (scroll-to-bottom $container))

(defn init [socket-out-pub]
  (let [socket-out-sub (sub socket-out-pub :console (chan))
        loading-spinner ($ :#clojure-spinner)
        loading-message ($ :#loading-message)]
    (go-loop [msg (<! socket-out-sub)]
      (if (= msg "REPL OK")  ;; TODO: listen to :command channel for repl ok, this no longer receives repl ok
        (fade-out loading-spinner 500)
        (append-text-to-container loading-message msg))
      (recur (<! socket-out-sub)))))
