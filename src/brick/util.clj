(ns brick.util
  "Utility functions for brick."
  (:require [quil.core :as q]))

(defn named-args
  "Parses a seq of arguments into a hash-map"
  [args]
  (into {} (map vec (partition 2 args))))

(defmacro with-scale
  "By tylergreen"
  [scale-vector & body]
  `(let [tr# ~scale-vector]
     (q/push-matrix)
     (apply q/scale tr#)
     ~@body
     (q/pop-matrix)))
