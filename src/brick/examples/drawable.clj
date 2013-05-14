(ns brick.examples.drawable
  (:use [quil.core :exclude [size]]
        brick.core
        brick.drawable)
  (:require [brick.image :as image])
  (:import [brick.drawable Bricklet]
           [brick.drawable Image]
           [brick.drawable Grid]
           [brick.drawable Stack])
  (:gen-class))

(def br (defbricklet
          (atom [])
          :layers-init (fn [bricklet]
                         (swap! (:layers bricklet)
                                (fn [old]                                  
                                  [(Grid. 2 1 {[0 0] (image/get-image-in bricklet :bricks)
                                               [1 0] (Stack. [(image/get-image-in bricklet :bush-l)
                                                              (Grid. 1 2 {[0 0] (image/get-image-in bricklet 0)
                                                                          [0 1] (image/get-image-in bricklet 7)})])})])))
          :execute-queue (atom [])
          :dictionary {:bricks 1
                       :bush-l 4
                       :bush-r 5}
          :images (atom [])
          :size [500 500]))

(brick-sketch a br)

(schedule br (fn []
              (background 0 100 0)))
