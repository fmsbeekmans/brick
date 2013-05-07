(ns brick.core
  (:use [quil.core])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [brick.debug :as debug])
  (:import [brick.layer GridLayer]))

(defn setup [tiles-atom]
  (smooth))

(def t (atom 0))

(defn draw [tiles-atom]
  (swap! tiles-atom
         (fn [tiles]
           (concat
            (tile/load-tiles
             (load-image "resources/tiles2.png") 32))))
  (spit "resources/log" (count @tiles-atom))
  
  (frame-rate 60)
  (with-translation [(/ (width) 2) (/ (height) 2) ]
    (with-rotation [@t]
      (with-translation [(+ 10 @t) 0]
        (spit "src/brick/log" @tiles-atom)
        ((nth @tiles-atom 7) @t @t))))
  (swap! t inc))


(defn start []
  (let [tiles (atom [])]
    (defsketch example
      :title "Tiles!"
      :setup #(setup tiles)
      :draw #((var draw) tiles)
      :size [320 320])))
