(defproject replme "0.1.0-SNAPSHOT"
  :description "repl me"
  :url "replme.clojurecup.com"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "replme"
              :source-paths ["src"]
              :compiler {
                :output-to "resources/public/replme.js"
                :output-dir "resources/public/out"
                :optimizations :none
                :source-map true}}]})

