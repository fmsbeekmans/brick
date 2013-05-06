(ns brick.core
  (:use [quil.core])
  (:require [brick.tile :as tile]
            [brick.layer :as layer])
  (:import [brick.layer GridLayer])
  (:gen-class))

(defn setup [tiles-atom]
  (tile/load-tiles tiles-atom (load-image "resources/tiles2.png") 32)
  (smooth)
  (frame-rate 1)
  (background 255))

(defn draw [tiles-atom]
  (.draw
   (background 0)
   (text (str "aoeu" (first @tiles-atom)) 20 20)
   (comment (layer/init-grid-layer 3 3
                                   (fn [x y]
                                     (fn [h w]
                                       (image (@tiles-atom 1) 0 0 h w)))))))

(defn start []
  (let [tiles (atom [])]
    (defsketch example
      :title "Tiles!"
      :setup #(setup tiles)
      :draw #((var draw) tiles)
      :size [320 320])))
