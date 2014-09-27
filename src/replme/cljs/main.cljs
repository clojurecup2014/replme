(ns replme.cljs.main
  (:use [jayq.core :only [$ css html document-ready]]))

(def $main ($ :#main))
(def $repl-container ($ :#repl-container))

(enable-console-print!)


(def repl-config (js-obj "promptLabel" ">>"
                         "autofocus" true
                         "animateScroll" true
                         "promptHistory" true))

(.console $repl-container repl-config)

(println "Repl Me.")

(document-ready (println "doc ready"))
