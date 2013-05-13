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
                                  [(image/get-image-in bricklet :bricks)])))
          :execute-que (atom [])
          :dictionary {:bricks 1
                       :bush-l 4
                       :bush-r 5}
          :images (atom [])
          :size [500 500]))

(brick-sketch a br)

