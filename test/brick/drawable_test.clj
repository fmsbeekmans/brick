(ns brick.drawable-test
  (:use midje.sweet)
  (:use [brick.image :only [load-images]])
  (:use [brick.util])
  (:require [brick.drawable :as d])
  (:require [quil.core :as q]))

(facts "ranges"
       (let [r (@#'d/ranges 4 25)]
         (fact "distance"
               (doseq [[offset width] r]
                 width => (roughly 25/4 1)))
         (fact "overlap"

               (loop [s 0
                      i 0]
                 s => (get-in r [i 0])
                 (if-not (< (inc i) (count r))
                   (do (+ (get-in r [3 1])
                          s) => 25)
                   (recur (+ s (get-in r [i 1]))
                          (inc i)))))
         (fact "end"
               (apply + (last r)) => 25)
         (fact "should be n partitions"
               (count r) => 4)
         (fact "start at 0"
               (first (first r)) => 0)))

(fact "drawable? on a non-drawable returns false."
  (d/drawable? 42) => FALSEY)

(fact "drawable? on a drawable returns true."
  (d/drawable? (d/->Nothing)) => TRUTHY)
