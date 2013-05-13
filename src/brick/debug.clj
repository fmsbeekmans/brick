(ns brick.debug
  (:use [quil.core]))

(defprotocol HUDDebug
  "Make a debugger on the current graphics."
  (toggle [_] "Turn the debug visible and invisible")
  (add-line [_ text derefable] "Add some extra information to the debugger.")
  (remove-line [_ derefable] "Remove a line from the debugger.")
  (draw [_] "Draw the debugger."))

(defn simple-dbg []
  "Create a new simple debugger on the current screen."
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
