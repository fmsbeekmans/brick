(ns brick.drawable
  (:use quil.core))

(defprotocol Drawable
  "Anything that can be drawn"
  (draw [this [h w]]))

(defrecord Image [tiles path]
  Drawable
  (draw [this [h w]]
    image (apply (:tiles this) (:path this)) h w))

(defrecord FloatingImage [image topleft scale orientation]
  Drawable
  (draw [this [h w]]))

(defrecord StackLayer [layers]
  Drawable
  (draw [this [h w]]
    (map draw (:layers this))))

(defrecord Bricklett [layers]
  Drawable
  (draw [this [h w]]))
