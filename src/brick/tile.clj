(ns brick.tile
  (:use quil.core))

(defn load-tiles
  "Return a function that given a keyword index map, returns a keyword
  p-image map"
  [tile-map-image tile-width]
  (let [num-tiles (/ (.width tile-map-image) tile-width)
        indexed-tiles
        (for [i (take num-tiles
                      (iterate (partial + tile-width) 0))]
          (.get tile-map-image i 0 tile-width tile-width))]
    (fn [key-index-map]
      (reduce merge
              (for [[k v] key-index-map
                    :let [tile ((vec indexed-tiles) v)]
                    :when tile]
                {k tile})))))
