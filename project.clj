(defproject replme "0.1.0-SNAPSHOT"
  :description "REPLME"
  :url "replme.clojurecup.com"

  :license {:name "GNU Affero General Public License v3"
            :url "https://www.gnu.org/licenses/agpl-3.0.html"}



  :dependencies [[org.clojure/clojure "1.7.0-alpha1"]
                 [http-kit "2.1.18"]
                 [compojure "1.1.9"]
                 [http.async.client "0.5.2"]
                 [docker "0.2.0"]
                 [org.slf4j/slf4j-simple "1.7.7"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/tools.logging "0.2.6"]
                 [com.stuartsierra/component "0.2.2"]
                 [org.clojure/tools.nrepl "0.2.5"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/clojurescript "0.0-2342"]
                 [markdown-clj "0.9.53"]
                 [jayq "2.5.2"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :profiles {:uberjar {:main replme.core
                       :aot [replme.core]
                       :hooks [leiningen.cljsbuild]}
             :dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]]}}
  :cljsbuild {
             :builds {:prod {:source-paths ["src"]
                              :compiler {:preamble ["libs/jquery-1.10.2.min.js"
                                                    "libs/jquery.console.js"]
                                         :externs ["libs/jquery-1.10.2.min.js"
                                                   "libs/jquery.console.js"]
                                         :output-to "resources/public/replme.js"
                                         :output-dir "resources/public/out"
                                         :pretty-print false
                                         :optimizations :advanced
                                         :closure-warnings {:externs-validation :off
                                                            :non-standard-jsdoc :off}}}
                       :dev {:source-paths ["src"]
                             :compiler {
                                        :output-to "resources/public/replme.js"
                                        :optimizations :none
                                        :source-map true}}}})
