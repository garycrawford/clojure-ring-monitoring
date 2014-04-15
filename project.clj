(defproject clojure-ring-monitoring "0.1.0-SNAPSHOT"
  :description "Add healthchecks to your app"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "4.0.1"]
                 [metrics-clojure "1.0.1" :exclusions [cheshire org.clojure/clojure]]
                 [metrics-clojure-ring "1.0.1" :exclusions [cheshire org.clojure/clojure]]
                 [org.clojure/tools.logging "0.2.3"]
                 [ch.qos.logback/logback-classic "1.1.1"]
                 [ring/ring-core "1.1.5" :exclusions [org.clojure/clojure]]
                 [hiccup "1.0.0" :exclusions [org.clojure/clojure]]]
  :plugins [[lein-swank "1.4.4"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]
                                  [ring-mock "0.1.5"]]}})
