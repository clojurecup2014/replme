(ns replme.cljs.loading-message
  (:require [cljs.core.async :refer [<! >! chan sub]]
            [replme.cljs.websocket :as ws])
  (:use [jayq.core :only [$ fade-out append slide-up prop anim]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

(defn scroll-to-bottom [$container]
  (let [scroll-height (prop $container "scrollHeight")]
    (anim $container {:scrollTop scroll-height} 100)))

(defn append-text-to-container [$container text]
  (append $container (str text))
  (scroll-to-bottom $container))

(defn init [socket-out-pub]
  (let [console-out-sub (sub socket-out-pub :console (chan))
        command-out-sub (sub socket-out-pub :command (chan))
        loading-spinner ($ :#clojure-spinner)
        loading-message ($ :#loading-message)]

    (go-loop [msg (<! command-out-sub)]
             (if (= (:message msg) "REPL OK")
               (do
                 (slide-up loading-message 500)
                 (fade-out loading-spinner 500)))
             (recur (<! command-out-sub)))

    (go-loop [msg (<! console-out-sub)]
             (append-text-to-container loading-message msg)
             (recur (<! console-out-sub)))))
