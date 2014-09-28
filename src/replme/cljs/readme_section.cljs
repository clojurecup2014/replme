(ns replme.cljs.readme-section
  (:use [jayq.core :only [$ on find fade-in html slide-up ajax done remove-class add-class]])
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

(defn get-text
  [ev]
  (or (.-textContent (.-target ev))
      (.-innerText (.-target ev))))

(defn init [comm-chan state out-chan]
  (let [$readme-container ($ :#readme-container)
        $readme-close (find $readme-container :.readme-close)
        $repl (find ($ :#repl-container) :.jquery-console-inner)
        repo-name (:repo state)]

    (on $readme-close :click (fn [e]
                               (slide-up $readme-container 500
                                         #(remove-class $repl "readme-open"))))
    
    (when-not (= repo-name "")
      (go
        (add-class $repl "readme-open")
        (-> $readme-container
            (fade-in 500)
            (find :.readme-content)
            (html (<! (load-readme repo-name)))
            (on :click :pre (fn [ev] (go (>! out-chan (get-text ev))))))))))
