(ns brick.drawable
  (:use quil.core))

(defprotocol Drawable
  "Anything that can be drawn"
  (draw [this [w h]]))

(defrecord Image [img]
  Drawable
  (draw [this [w h]]
    (image (:img this) 0 0 w h)))

(defrecord FloatingImage [image topleft scale orientation]
  Drawable
  (draw [this [w h]]))

(defrecord StackLayer [layers]
  Drawable
  (draw [this [w h]]
    (doseq [layer (:layers this)]
      (.draw layer [w h]))))

(defrecord Grid [w h grid]
  Drawable
  (draw [this [w h]]
    (doseq [x (range (:w this))
            y (range (:h this))]
      (text (pr-str [(* x (/ w (:w this)))]) 20 (+ 40 (* x 20)))
      (with-translation [(* x (/ w (:w this)))
                         (* y (/ h (:h this)))]
        (.draw ((:grid this) [x y]) [(/ w (:w this))
                                     (/ h (:h this))])))))

(defrecord Bricklet [layers]
  Drawable
  (draw [this [w h]]
    (doseq [layer @(:layers this)]
      (.draw layer [w h]))))
