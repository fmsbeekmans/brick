(ns brick.t-drawable
  (:use midje.sweet)
  (:use [quil.core :only [load-image]])
  (:use [brick.core :only [bricklet-sketch]])
  (:require [brick.drawable :as d]))

(facts "ranges"
       (let [r (@#'d/ranges 4 25)]
         (fact "distance"
               (doseq [[offset width] r]
                 width => (roughly 25/4 1)))
         (future-fact "overlap"
               (doseq [[offset width] r]
                 ))
         (fact "end"
               (apply + (last r)) => 25)
         (fact "should be n partitions"
               (count r) => 4)
         (fact "start at 0"
               (first (first r)) => 0)))

(let [layers (atom [])
      images (atom [])
      dictionary {:red 0
                  :green 1}
      layers-init (fn [old]
                    [(d/->Stack (d/->Image [(dictionary :red)]))
                     (d/->Stack (d/->Image [(dictionary :green)]))])
      setup (fn [bricklet]
             (reset! (:layers bricklet) layers-init))]
  (bricklet-sketch
   (d/->Bricklet (atom []) (atom []))
   :setup setup))

(facts "Stack"
  (fact "Layer 0 is drawn before layer 1."))

(facts "Grid"
  (fact "All tiles are drawn at the correct spot.")
  (fact "Irrelevant entries in the tiles map are ignored."))

(facts "Bricklet")
