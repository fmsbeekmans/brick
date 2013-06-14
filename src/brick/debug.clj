(ns brick.debug
  "A visual debugger. Add derefables, their derefed
string value will be printed."
  (:use [quil.core])
  (:require [brick.drawable :as drawable]))

(defprotocol HUDDebug
  "Make a debugger on the current graphics."
  (toggle [_] "Turn the debug visible and invisible")
  (add-line [_ text derefable] "Add some extra information to the debugger.")
  (remove-line [_ derefable] "Remove a line from the debugger."))

(defn simple-dbg []
  "Create a new simple debugger"
  (let [visible? (atom false)
        lines (atom {})]
    (reify
      drawable/Drawable
      (drawable/draw [_ [_ _]]
        (when @visible?
            (push-style)
            (doall (map
                    (fn [[d t] i]
                      (fill 90 170)
                      (stroke 0)
                      (rect 20 (+ 5 (* 20 i)) 300 20 )
                      (fill 255)
                      (text (str t ": " (pr-str @d)) 20 (+ 20 (* 20 i))))
                    @lines (iterate inc 0)))
            (pop-style)))
      HUDDebug
      (toggle [_]
        (swap! visible? not))
      (add-line [_ text derefable]
        (swap! lines #(assoc % derefable text)))
      (remove-line [_ derefable]
        (swap! lines #(dissoc % derefable))))))
