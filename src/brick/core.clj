(ns brick.core
  (:use [quil.core])
  (:require [brick.tile :as tile]
            [brick.debug :as debug])
  (:gen-class))

(defn setup []
  (smooth)
  (frame-rate 1)
  (background 255)
  (def dbg ( debug/fps-dbg))
  (debug/toggle dbg)
  (def tiles
    ((tile/load-tiles (load-image "resources/tiles2.png") 32)
     {:bricks 1
      :grass 2
      :bush 4
      :shroomr 11
      :shrooml 9})))

(defn draw-grid []
  (doall
   (for [x (range 10)
         y (range 10)]
     (image (tiles
             (if (odd? (+ x y))
               :shrooml
               :shroomr)) (* x 32) (* y 32))))
  (with-translation [10 10] (image (tiles :bush) (* 32 (rand-int 10)) (* 32 (rand-int 10))))
  ;(debug/draw dbg)
  )


(defn draw-layer
  )

(defn start []
  (defsketch example
    :title "Tiles!"
    :setup setup
    :draw (var draw-grid)))

(defn -main []
  )
