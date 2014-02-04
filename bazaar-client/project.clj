(defproject bazaar-client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha" :scope "provided"]
                 [org.clojure/clojurescript "0.0-2156" :scope "provided"]
                 [om "0.3.5"]]

  :source-paths ["src"]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :profiles {
             :dev {
                   :plugins [[com.cemerick/austin "0.1.3"]]
                   :cljsbuild {
                               :builds [{:id "dev"
                                         :source-paths ["src"]
                                         :compiler {:output-to "main.js"
                                                    :output-dir "out"
                                                    :optimizations :none
                                                    :source-map true}}]}}})
