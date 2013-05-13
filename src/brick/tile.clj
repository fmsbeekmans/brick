(ns brick.tile
  (:use quil.core))

(defn load-images
  "Return a function that given a keyword index map, returns a keyword
p-image map"
  [source-image tile-width]
  (let [n-tiles (/ (.width source-image) tile-width)
        indexed-tiles
        (for [i (take n-tiles
                      (iterate (partial + tile-width) 0))]
          (fn [w h]
            (image
             (.get source-image i 0 tile-width tile-width)
             0 0
             w h)))]
    (vec indexed-tiles)))

(defmacro create-p-image
  [[w h] & body]
  `(let [graph# (create-graphics ~w ~h :java2d)]
     (with-graphics graph#
       (do ~@body))
     graph#))

(defmacro get-image-in
  [images dictionary & path]
  `(~images (~dictionary ~@path)))
