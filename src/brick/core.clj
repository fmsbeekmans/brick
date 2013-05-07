(ns brick.core
  (:use [quil.core])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [brick.debug :as debug])
  (:import [brick.layer GridLayer]))

(defn setup [tiles-atom]
    (swap! tiles-atom
         (fn [tiles]
           (concat tiles
                   (tile/load-tiles
                    (load-image "resources/tiles2.png") 32))))
    (smooth))

(def t (atom 0))

(defn draw [tiles-atom]
  (tile/with-tiles (vec @tiles-atom)
    (tile/with-dictionary {:paths {:road 0
                                   :canal 3
                                   :railroad 5}
                             :b 1
                             :c 2}
        (with-translation [100 100]
          (background 0)
          ((tile/tile :paths :canal) 20 20)))))


(defn start []
  (let [tiles (atom [])]
    (defsketch example
      :title "Tiles!"
      :setup #(setup tiles)
      :draw #((var draw) tiles)
      :size [320 320])))
