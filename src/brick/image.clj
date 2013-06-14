(ns brick.image
  "Helper functions for things to do with PImages."
  (:use quil.core)
  (:require [brick.drawable :as draw])
  (:import [brick.drawable Image]))

(defn load-images
  "Cut a source image up into pieces of tile-w by tile-h and put them in a vector."
  [source-image [tile-w tile-h]]
  (let [n-tiles (/ (.width source-image) tile-w)
        indexed-tiles
        (for [i (take n-tiles
                      (iterate (partial + tile-w) 0))]
          (Image. (.get source-image i 0 tile-w tile-h)))]
    (vec indexed-tiles)))

(defmacro in-draw-context
  [expr]
  `(let [p-expr# (promise)]
     (sketch :target :none
             :setup
             (fn [] (deliver p-expr# ~expr)))
     @p-expr#))

(defn path->PImage
 [path]
 (in-draw-context (load-image path)))

;;TODO resource-image should return a record containing a dictionary key, rather than a PImage. This makes the record comparable and comparable is good.
(defn resource-image [path]
  (draw/->Image (path->PImage (clojure.java.io/resource path))))
