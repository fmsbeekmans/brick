(ns brick.t-core
  (:use midje.sweet)
  (:use [brick.core]))

(facts "brick initializes"
       (fact "title is correct")
       (fact "setup is ran")
       (fact "draw is called")
       (fact "size is correct")
       (fact "tile atom is used")
       (fact "layers atom is used"))

(fact "render draws all layers in order")

(fact "update works")
