(defproject replme "0.1.0-SNAPSHOT"
  :description "repl me"
  :url "replme.clojurecup.com"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [http-kit "2.1.18"]
                 [compojure "1.1.9"]
                 [liberator "0.12.2"]
                 [docker "0.2.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/tools.logging "0.2.6"]
                 [com.stuartsierra/component "0.2.2"]
                 [org.clojure/tools.nrepl "0.2.5"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [jarohen/chord "0.4.2"]
                 [jayq "2.5.2"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :profiles {:uberjar {:main replme.core
                       :aot [replme.core]}

             :dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]]}}
  :cljsbuild {
              :builds [{:id "replme"
                        :source-paths ["src"]
                        :externs ["resources/public/externs/jquery.js",
                                  "resources/public/externs/jquery_console.js"]
                        :compiler {
                                   :output-to "resources/public/replme.js"
                                   :output-dir "resources/public/out"
                                   :optimizations :none
                                   :source-map true}}]})
