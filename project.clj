(defproject brick "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [quil "1.6.0"]]
  :resource-paths ["resources/"]
  :profiles {:dev {:plugins [[lein-midje "3.0.0"]
                             [lein-cloverage "1.0.2"]]
                   :dependencies [[midje "1.5.0"]]}}
  :main brick.core
  :aot [quil.applet])
