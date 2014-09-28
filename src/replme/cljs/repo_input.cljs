(ns replme.cljs.repo-input
  (:use [jayq.core :only [$ on val]])
  (:require [cljs.core.async :refer [>!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def $repo-input-form ($ :#repo-input-form))
(def $repo-input ($ :#repo-input))

(enable-console-print!)

(defn clear-input [input]
  (val input ""))

(defn on-repo-submit
  [control-chan]
  (fn
    [e]
    (.preventDefault e)
    (go (>! control-chan (val $repo-input))
        (clear-input $repo-input))))

(defn init [control-chan]
  (on $repo-input-form :submit (on-repo-submit control-chan)))

