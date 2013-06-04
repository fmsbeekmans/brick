(ns brick.util
  (:use quil.core))

(defn name-dargs
  "Parses a seq of arguments into a hash-map"
  [args]
  (into {} (map vec (partition 2 args))))

(defmacro with-scale [scale-vector & body]
  "By tylergreen"
  `(let [tr# ~scale-vector]
     (push-matrix)
     (apply scale tr#)
     ~@body
     (pop-matrix)))
