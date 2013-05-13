(ns brick.t-drawable
  (:use midje.sweet)
  (:use [brick.drawable]))

(facts "ranges"
  (fact "distance")
  (fact "overlap")
  (fact "end"))

(facts "Floating image"
  ())

(facts "Stack"
  (fact "Layer 0 is drawn before layer 1."))

(facts "Grid"
  (fact "All tiles are drawn at the correct spot.")
  (fact "Irrelevant entries in the tiles map are ignored."))

(facts "Bricklet")
