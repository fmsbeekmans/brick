(ns brick.util
  (:use quil.core))

(defmacro with-scale [scale-vector & body]
  "By tylergreen"
  `(let [tr# ~scale-vector]
     (push-matrix)
     (apply scale tr#)
     ~@body
     (pop-matrix)))
