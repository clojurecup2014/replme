(ns replme.cljs.repo-input
  (:use [jayq.core :only [$ on val]]))

(def $repo-input-form ($ :#repo-input-form))
(def $repo-input ($ :#repo-input))

(enable-console-print!)

(defn clear-input [input]
  (val input ""))

(defn on-repo-submit [e]
  (.preventDefault e)
  (clear-input $repo-input)
  (println "call a function to handle submit here..."))

(defn init []
  (on $repo-input-form :submit on-repo-submit))

