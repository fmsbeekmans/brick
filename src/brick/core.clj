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
  [bricklet & args]
  (let [defaults
        {:draw #(draw bricklet [ (width) (height)])
         :setup (fn [])
         :size [100 100]
         :title "No title"}
        opts (merge defaults (namedargs args))]
    (apply sketch (apply concat opts))))
