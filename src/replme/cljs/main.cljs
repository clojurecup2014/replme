(ns replme.cljs.main
  (:use [jayq.core :only [$ css html document-ready]]))

(def $main ($ :#main))
(def $repl-container ($ :#repl-container))

(enable-console-print!)

(def repl-config (js-obj "promptLabel" ">>"
                         "autofocus" true
                         "welcomeMessage" "Welcome! Type in some clojure to get started"
                         "animateScroll" true
                         "commandHandle" (fn [line] (array (js-obj "msg" line "className" "jquery-console-message-value")))
                         "promptHistory" true))


(defn setup-console []
  (.console $repl-container repl-config))

(println repl-config)

(document-ready (setup-console))
