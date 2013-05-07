(ns brick.debug
  (:use [quil.core]))

(defprotocol HUDDebug
  (toggle [_])
  (add-line [_ text derefable])
  (remove-line [_ derefable])
  (draw [_]))

(defn simple-dbg []
  (let [visible? (atom false)
        lines (atom {})]
    (reify HUDDebug
      (toggle [_]
        (swap! visible? not))
      (add-line [_ text derefable]
        (swap! lines #(assoc % derefable text)))
      (remove-line [_ derefable]
        (swap! lines #(dissoc % derefable)))
      (draw [_]
        (if @visible?
          (do
            (push-style)
            (doall (map
                    (fn [[d t] i]
                      (fill 90 170)
                      (stroke 0)
                      (rect 20 (+ 5 (* 20 i)) 300 20 )
                      (fill 255)
                      (text (str t ": " @d) 20 (+ 20 (* 20 i))))
                    @lines (iterate inc 0)))
            (pop-style)))))))
