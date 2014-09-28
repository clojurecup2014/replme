(ns replme.cljs.repo-input
  (:use [jayq.core :only [$ on val slide-down slide-up html]]
        [jayq.util :only [wait]])
  (:require [cljs.core.async :refer [>!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(def $repo-input-form ($ :#repo-input-form))
(def $repo-input ($ :#repo-input))

(enable-console-print!)

(defn clear-input [input]
  (val input ""))

(defn display-loading-info []
  (let [loading-message ($ :#loading-message)]
    (-> loading-message
        (slide-down 450)
        (html (str "loading repo " (val $repo-input) "...")))))

(defn on-repo-submit
  [control-chan]
  (fn
    [e]
    (.preventDefault e)
    (go (>! control-chan (val $repo-input))
        (display-loading-info)
        (clear-input $repo-input))))

(defn init [control-chan]
  (on $repo-input-form :submit (on-repo-submit control-chan)))

