(ns brick.drawable-test
  (:use midje.sweet)
  (:use [brick.image :only [load-images]])
  (:use [brick.core :only [bricklet-sketch]])
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

(fact "Stack"
    (let [dictionary {:half-red 0
                      :green 1
                      :red 2
                      :green-transparant 3}
          layers-init (fn [br]
                        (let [img #(@(:images br) (dictionary %))]
                          (d/->Stack [(img :green)
                                      (img :half-red)])))
          images-init (fn [br]
                        (load-images (q/load-image "test-resources/2x1.png") [2 1]))
          init (fn [br]
                 (q/background 0)
                 (reset! (:images br) (images-init br))
                 (reset! (:target-drawable br) (layers-init br)))
          br (d/->Bricklet (atom [])
                        (atom [])
                        :size [2 1]
                        :init init
                        :target :none
                        :dictionary dictionary)
          sketch (bricklet-sketch br)])
  (future-fact "Layer 0 is drawn before layer 1.")
  (future-fact "Transperancy works in images.")
  (future-fact "empty stack draws")
  (future-fact "empty stack doensn't change sketch."))

(facts "Grid"
  (fact "All tiles are drawn at the correct spot.")
  (fact "Irrelevant entries in the tiles map are ignored."))

(facts "Bricklet")
