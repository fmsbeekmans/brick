(defproject jest/brick "0.1.0"
  :description "Tiling engine built on Quil"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [quil "1.6.0"]]
  :resource-paths ["resources/"]
  :profiles {:dev {:plugins [[lein-midje "3.0.0"]
                             [codox "0.6.4"]
                             [lein-bikeshed "0.1.3"]
                             [jonase/eastwood "0.0.2"]
                             [lein-kibit "0.0.8"]
                             [lein-cloverage "1.0.2"]]
                   :dependencies [[midje "1.5.0"]
                                  [org.clojars.runa/conjure "2.1.3"]]}}
  :aot [quil.applet])
