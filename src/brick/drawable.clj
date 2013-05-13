(ns brick.drawable
  (:use quil.core))

(defn- ranges [n pixels]
  "Return a list of [offset size] so that pixels is devided into n pieces."
  (let [size (/ pixels n)]
    (vec
     (for [i (range n)]
       (let [start (int (Math/ceil (* i size)))
             end (int (Math/ceil (* (inc i) size)))]
         [start (- end start)])))))

(defprotocol Drawable
  "Anything that can be drawn"
  (draw [this [w h]]
    "Draw the given object w times h pixels large."))

(defrecord Image
    #^{:doc "A drawable wrapper around a PImage. "}
  [img]
  Drawable
  (draw [this [w h]]
    "Draw the current image w times h pixels large."
    (image (:img this) 0 0 w h)))

(defrecord FloatingImage
    #^{:doc "A floating image."}
  [image topleft scale orientation]
  Drawable
  (draw [this [w h]] "Draw this images at the right location."))

(defrecord Stack [layers]
  #^{:doc "A stack of drawables on top of one another."}
  Drawable
  (draw [this [w h]]
    (doseq [layer (:layers this)]
      (.draw layer [w h]))))

(defrecord Grid [w h grid]
  #^{:doc "A grid of drawables exactly side by side."}
  Drawable
  (draw [this [w h]]
    "Draw all the tiles next to one another."
    (let [h-ranges (ranges (:w this) w)
          v-ranges (ranges (:h this) h)]
      (doseq [x (range (:w this))
              y (range (:h this))]
        (with-translation [(get-in h-ranges [x 0])
                           (get-in v-ranges [y 0])]
          (.draw ((:grid this) [x y])
                 [(get-in h-ranges [x 1])
                  (get-in v-ranges [y 1])]))))))

(defrecord Bricklet [layers]
  #^{:doc "A special stacklayer."}
  Drawable
  (draw [this [w h]]
    "Draw the applet."
    (doseq [layer @(:layers this)]
      (.draw layer [w h]))
    (doseq [command @(:execute-que this)]
      (command))
    (reset! (:execute-que this) []))) ;vec, conjed on the end so the
                                      ;commands execute in order.
 
