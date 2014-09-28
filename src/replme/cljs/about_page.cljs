(ns replme.cljs.about-page
  (:use [jayq.core :only [$ on fade-in fade-out]]))

(defn init []
  (let [$site-title ($ :#site-title)
        $about-container ($ :#about-container)]
    (on $site-title "click" #((fade-in $about-container 500)))
    (on $about-container "click" #((fade-out $about-container 500)))))
