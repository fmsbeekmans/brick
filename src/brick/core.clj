(ns brick.core
  (:use [quil.core])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [brick.debug :as debug])
  (:import [brick.layer GridLayer]))

(declare setup)
(declare draw)


(def t (atom 0))

(defn start []
  (brick "Tyler" [640 320] #() (atom []) nil))

(defn brick [title size tile-init layers tile-atom command-que]
  (defsketch example
    :title title
    :setup  (fn [] (setup tiles layers))
    :draw ((var draw) tiles layers)
    :size size))

(defn setup [tiles-atom]
    (swap! tiles-atom
         (fn [tiles]
           (concat tiles
                   (tile/load-tiles
                    (load-image "resources/tiles2.png") 32))))
    (smooth))

(defn draw [tiles-atom layers]
  (tile/with-tiles (vec @tiles-atom)
    (tile/with-dictionary {:paths {:road 0
                                   :canal 3
                                   :railroad 5}
                             :b 1
                             :c 2}
      
        (with-translation [100 100]
          (background 0)
          ((tile/tile :paths :canal) 20 20)))))
