(ns brick.layer
  (:use quil.core))

(defprotocol Layer
  (draw [_ _]))

(defrecord GridLayer [h w tiles cache?]
  Layer
  (draw [this cache]
    (if (cache? this)
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
           )))

(defn init-grid-layer [h w init-fn]
  (GridLayer. h w
              (apply hash-map
                     (mapcat
                      (fn [[[x y] v]]
                        [[x y] v])
                      (doall (for [x (range w)
                                   y (range h)]
                               [[x y] (init-fn x y)]))))
              #(false)))
