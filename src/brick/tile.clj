(ns brick.tile
  (:use quil.core))

(defn load-tiles
  "Return a function that given a keyword index map, returns a keyword
  p-image map"
  ([source-image tile-width]
     (load-tiles (atom []) source-image tile-width))
  ([tiles source-image tile-width]
     (let [n-tiles (/ (.width source-image) tile-width)
           indexed-tiles
           (for
               [i (take n-tiles
                        (iterate (partial + tile-width) 0))]
             (fn [h w]
               (image
                (.get source-image i 0 tile-width tile-width)
                      0 0
                      h w)))]
       (vec indexed-tiles))))


