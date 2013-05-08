(ns brick.layer
  (:use quil.core))

(defprotocol Layer
  "A layer that can be drawn in a bricklett."
  (draw [_ _])
  (update [_]))

(defrecord GridLayer
; "A Layer that consists of a h times w grid of tiles"
  [h w tiles update cache?]
  Layer
  (draw [this cache]
    (if (not (cache? this))
      (do
        (let [tile-h (/ (height) (:h this))
              tile-w (/ (width) (:w this))]
          (doall
           (for [x (range (:w this))
                 y (range (:h this))]
             (with-translation [(* x tile-w)
                                (* y tile-h)]
               (((:tiles this) [x y]) tile-w tile-h)))))
        (do
          ;draw from cache
          )))
    (cache [this]
           ;save rendering of this layer alone.
           ))
  (update [this]
    ((:this update))))

(defn init-grid-layer
  "Initialize a new grid"
  [h w init-fn]
  {:pre [(>= (* h w) 0)]}
  (GridLayer. h w
              (apply hash-map
                     (mapcat
                      (fn [[[x y] v]]
                        [[x y] v])
                      (doall (for [x (range w)
                                   y (range h)]
                               [[x y] (init-fn x y)]))))
              #()
              #(false)))

(defn set-tile
  "Change tile [x y]"
  [layer [x y] tile]
  {:pre [brick.bricklett]}
  (assoc (:tiles layer) [x y] tile))
