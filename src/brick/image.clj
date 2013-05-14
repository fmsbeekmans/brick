(ns brick.image
  (:use quil.core)
  (:require [brick.drawable :as draw])
  (:import [brick.drawable Image]))

(defn load-images
  "Return a function that given a keyword index map, returns a keyword
p-image map"
  [source-image [tile-w tile-h]]
  (let [n-tiles (/ (.width source-image) tile-w)
        indexed-tiles
        (for [i (take n-tiles
                      (iterate (partial + tile-w) 0))]
          (Image. (.get source-image i 0 tile-w tile-h)))]
    (vec indexed-tiles)))

(defmacro get-image-in
  "Get an image from the bricklet's dictionary from the bricklet's image library."
  [bricklet & path]
  `(nth @(:images ~bricklet)
        (dictionary (:dictionary ~bricklet) ~@path)))

(defn dictionary [map key]
  (or (map key)
      key))
