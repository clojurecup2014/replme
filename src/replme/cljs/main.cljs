(ns replme.cljs.main
  (:use [jayq.core :only [document-ready]])
  (:require [replme.cljs.repl :as repl]))

(document-ready
  (repl/init))
