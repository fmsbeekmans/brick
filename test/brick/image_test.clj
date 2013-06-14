(ns brick.image-test
  (:use midje.sweet)
  (:use [quil.core :exclude [background]])
  (:require [brick.image :as i]))

(fact "load-images"
  (class (i/resource-image "32x32.png")) => brick.drawable.Image)

(fact "path->PImage"
  (class (i/path->PImage "32x32.png")) => processing.core.PImage)

(fact "load-images."
  (i/in-draw-context
   (let [images (i/load-images (i/path->PImage "32x32.png") [32 32])]
     (fact "Are they in a vector?"
       images => vector?)
     (fact "Are there 14 images?"
       (count images) => 14))))
