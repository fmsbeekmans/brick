(ns brick.layer
  (:use quil.core))

(defprotocol Layer
  "A layer that can be drawn in a bricklett."
  (draw [_])
  (update [_]))

(defrecord GridLayer
; "A Layer that consists of a h times w grid of tiles"
  [h w tiles dictionary update cache?]
  Layer
  (draw [this]
    (let [tile-h (/ (height) (:h this))
           tile-w (/ (width) (:w this))]
      (doall
       (for [x (range (:w this))
             y (range (:h this))]
         (with-translation [(Math/ceil (* x tile-w))
                            (Math/ceil (* y tile-h))]
           (((:tiles this) [x y]) (Math/floor tile-w) (Math/floor tile-h)))))))
  (update [this]
    ((:update this))))

(defn init-grid-layer
  "Initialize a new grid"
  [h w init-fn dictionary]
  {:pre [(>= (* h w) 0)]}
  (GridLayer. h w
              (apply hash-map
                     (mapcat
                      (fn [[[x y] v]]
                        [[x y] v])
                      (doall (for [x (range w)
                                   y (range h)]
                               [[x y] (init-fn x y)]))))
              dictionary
              #()
              #(false)))

(defn set-tile
  "Change tile [x y]"
  [layer [x y] tile]
  (assoc (:tiles layer) [x y] tile))
