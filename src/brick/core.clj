(ns brick.core
  (:use [quil.core :exclude [size]])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [brick.debug :as debug]
            [brick.drawable :as drawable])
  (:import [brick.drawable Bricklett]))

(defmacro defbricklett
  [layers & args]
  `(let [bricklett# (tile/Bricklett. ~layers)]
     (assoc bricklett# ~@args)))

(defmacro brick-sketch
  [sym bricklett]
  `(defsketch ~sym
     :draw (or (:draw ~bricklett)
               #())
     :setup (or (:setup ~bricklett)
                #())
     :size (or (:size ~bricklett)
               [200 200])
     :title (or (:title ~bricklett)
                "")))

