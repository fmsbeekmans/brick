(ns brick.t-core
  (:use midje.sweet)
  (:use [brick.core]))

(facts :gui "brick initializes"
       (fact "title is correct")
       (fact "setup is ran")
       (fact "draw is called")
       (fact "size is correct")
       (fact "tile atom is used")
       (fact "layers atom is used"))

(facts "can't call certain methods without running bricklett.")

(fact :gui "render draws all layers in order")

(fact :gui "update works")
