(ns replme.cljs.readme-section
  (:use [jayq.core :only [$ on find fade-in html fade-out ajax done add-class]])
  (:require [goog.crypt.base64 :as b64]
            [markdown.core :refer [md->html]]
            [clojure.string :as str]
            [cljs.core.async :refer [<! >! chan close!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn readme-url [owner project]
  (str "https://api.github.com/repos/" owner "/" project "/readme"))

(defn load-readme [repo]
  (let [out (chan)
        [owner project] (str/split repo "/")]
    (done
     (ajax (readme-url owner project))
     (fn [data]
       (go
        (>! out (-> (.-content data)
                    (b64/decodeString)
                    (md->html))))))
    out))

(defn init [comm-chan state]
  (let [$readme-container ($ :#readme-container)
        $readme-close (find $readme-container :.readme-close)
        $repl (find ($ :#repl-container) :.jquery-console-inner)
        repo-name (:repo state)]

    (on $readme-close :click #(fade-out $readme-container 500))

    (when-not (= repo-name "")
      (go
       (add-class $repl "readme-open")
       (-> $readme-container
           (fade-in 500)
           (find :.readme-content)
           (html (<! (load-readme repo-name))))))))

