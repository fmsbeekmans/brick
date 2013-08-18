(defproject jest/brick "0.1.2"
  :description "Tiling engine built on Quil"
  :url "http://www.github.com/fmsbeekmans/brick.git"
  :license {:name "Eclipse Public License - v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
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
  :codox {:src-dir-uri "https://github.com/fmsbeekmans/brick/blob/master"
          :src-linenum-anchor-prefix "L"}
  :aot [quil.applet])
