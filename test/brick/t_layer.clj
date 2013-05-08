(ns brick.layer.t-layer
  (:use midje.sweet)
  (:use [brick.layer]))

(fact "Reify a layer")

(facts "GridLayer"
       (fact "Initialized well?")
       (fact "Can it be drawn?")
       (fact "update is called")
       (fact "update is proccessed"))

(fact "init-grid-layer works")

(fact "set-tile updates the correct tile.")
