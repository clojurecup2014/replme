(ns replme.cljs.repo-input
  (:use [jayq.core :only [$ on val slide-down slide-up html]]
        [jayq.util :only [wait]]))

(def $repo-input-form ($ :#repo-input-form))
(def $repo-input ($ :#repo-input))

(enable-console-print!)

(defn clear-input [input]
  (val input ""))

(defn display-loading-info []
  (let [loading-message ($ :#loading-message)]
   (-> loading-message
       (slide-down 450)
       (html (str "loading repo " (val $repo-input) "..."))) ;; this'll become a stream of data
   (wait 3000  (fn [] (slide-up loading-message 500))))) ;; this'll be a done fn

(defn on-repo-submit [e]
  (.preventDefault e)
  (display-loading-info)
  (clear-input $repo-input)
  )

(defn init []
  (on $repo-input-form :submit on-repo-submit))

