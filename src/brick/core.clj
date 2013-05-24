(ns brick.core
  (:use [quil.core :exclude [size]]
        brick.drawable)
  (:require [brick.image :as image]
            quil.applet)
  (:gen-class))

(defn- namedargs
  "Parses a seq of arguments into a hash-map"
  [args]
  (into {} (map vec (partition 2 args))))

(defn bricklet-sketch
  "Creates a sketch from a bricklet and quil options"
  [bricklet]
  (apply sketch (apply concat
                       (assoc bricklet
                         :setup #((:init bricklet) bricklet)
                         :draw (fn []
                                 (.draw bricklet [(width) (height)]))))))
