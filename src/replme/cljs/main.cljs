(ns replme.cljs.main
  (:use [jayq.core :only [document-ready]])
  (:require [replme.cljs.repo-input :as repo-input]
            [replme.cljs.repl :as repl]))

(defn initialize-app []
  (repl/init)
  (repo-input/init))

(document-ready initialize-app)
