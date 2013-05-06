(ns brick.layer)

(defprotocol Layer
  (draw [_]))

(defrecord GridLayer [:h :w :tiles]
  Layer
  (draw [this]
    (doall
     (for [x (range (:w this))
           y (range (:h this))]))))
