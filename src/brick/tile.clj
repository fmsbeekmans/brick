(ns brick.tile
  (:use quil.core))

(def ^:dynamic dictionary [])
(def ^:dynamic tiles [])

(defn load-tiles
  "Return a function that given a keyword index map, returns a keyword
  p-image map"
   [source-image tile-width]
     (let [n-tiles (/ (.width source-image) tile-width)
           indexed-tiles
           (for [i (take n-tiles
                         (iterate (partial + tile-width) 0))]
             (fn [h w]
               (image
                (.get source-image i 0 tile-width tile-width)
                      0 0
                      h w)))]
       (vec indexed-tiles)))

(defn tile
  "Return a tile from the tiles given by it's path in the dictionary."
  [& path]
  {:pre [()]}
  (tiles (get-in dictionary path)))

(defmacro with-tiles [local-tiles & body]
  "Execute the body using a tile dictionary."
  `(binding [tiles ~local-tiles]
     (do ~@body)))

(defmacro with-dictionary [local-dictionary & body]
  "Execute the body using a tile dictionary."
  `(binding [dictionary ~local-dictionary]
     (do ~@body)))

(defn image-tile
  [& path]
  (fn [w h]
    (image (get-in dictionary path)
           0 0
           w h)))
