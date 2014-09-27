(ns replme.cljs.main
  (:use [jayq.core :only [$ css html document-ready]]))

(def $repl-container ($ :#repl-container))

(enable-console-print!)

(defn handle-command [input report]
  (let [line input]
    ;; evaluate line here and return that the as the value of "msg"...
    (array (js-obj
            "msg" line
            "className" "jquery-console-message-value"))))

(def repl-config (js-obj "promptLabel" ">>"
                         "autofocus" true
                         "welcomeMessage" "Welcome! Type in some clojure to get started"
                         "animateScroll" true
                         "commandHandle" handle-command
                         "promptHistory" true))

(defn setup-console []
  (.console $repl-container repl-config))

(println repl-config)

(document-ready (setup-console))
