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

(defmacro new-p-image
  [[w h] & body]
  "Create a new p image of w by h pixels and draw body on it."
  `(let [graph# (create-graphics ~w ~h :java2d)]
     (with-graphics graph#
       (do ~@body))
     graph#))

(defn append-images!
  [new [w h] tiles]
  "Append the image new to the list of tiles of size wxh.png, create one if none exists."
  (io!
   (let [orig (or
               (load-image (str "resources/" w "x" h ".png"))
               nil)
         i (if orig
             (/ (.width orig) w)
             0)
         graph (create-graphics (* (inc i) w) h :java2d)]
     (if orig (with-graphics graph

                (if orig
                  (image orig 0 0))
                (image new (.width orig) 0 w h)))
     (.save graph  (str "resources/" w "x" h ".png"))
     (swap! tiles conj new)
     i)))

(defmacro get-image-in
  "Get an image from the bricklet's dictionary from the bricklet's image library."
  [bricklet & path]
  `(nth @(:images ~bricklet)
        (dictionary (:dictionary ~bricklet) ~@path)))

(defn dictionary [map key]
  (or (map key)
      key))
