(ns brick.drawable-test
  (:use midje.sweet
        conjure.core)
  (:use [brick.image :only [load-images]])
  (:use [brick.util])
  (:require [brick.drawable :as d])
  (:require [quil.core :as q])
  (:import [brick.drawable Drawable]))

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

(fact "drawable->sketch has setup and draw fns"
      (with-redefs [q/sketch (fn [& args]
                               (let [arg-map (named-args args)]
                                 (fact
                                  "Contains setup fn"
                                  arg-map => (contains {:setup fn?})
                                  ((arg-map :setup) nil) => (throws clojure.lang.ArityException))
                                 (fact
                                  "Contains draw fn"
                                  arg-map => (contains {:draw fn?})
                                  ((arg-map :draw) nil) => (throws clojure.lang.ArityException))))]
        (d/drawable->sketch (d/->Nothing))))

(fact "drawable->sketch uses :init"
      (with-redefs [q/sketch (fn [& args]
                               (let [arg-map (named-args args)]
                                 ((:init arg-map))))]
        (d/drawable->sketch (d/->Bricklet (atom [])
                                          (atom [])
                                          :init (fn []
                                                  anything => anything)))))

(with-redefs [q/sketch (fn [& args]
                         (let [arg-map (named-args args)]
                           ((:init arg-map))))]
  (d/drawable->sketch (d/->Bricklet (atom [])
                                    (atom [])
                                    :init (fn []
                                            (fact "drawable->sketch uses :init"
                                              anything => anything))
                                    :draw (fn []
                                            (fact "drawable->sketch overrides :draw"
                                              true => falsey)))))

(fact "number to radials"
  (d/rad 1) => Math/PI
  (d/rad 2) => (* 2 Math/PI))

(defrecord Insert [insert into]
    Drawable
    (draw [this [w h]]
      (swap! (:into this) (fn [old]
                            (conj old (:insert this))))))

(with-redefs [q/with-translation (fn [tr body]
                                   (do body))
              q/push-matrix (fn [])
              q/pop-matrix (fn [])
              q/translate (fn [& _])
              q/width (fn [] 10)
              q/height (fn [] 10)
              q/sketch (fn [& args]
                         (let [arg-map (named-args args)]
                           ((:draw arg-map))))]
  ;; Stack test
  (let [v (atom [])
        s (d/->Stack (vec (for [i (range 4)]
                            (->Insert i v))))]
    (d/drawable->sketch s)
    (fact "All layers are drawn"
          (count @v) => 4)
    (fact "Layers are drawn in order"
          @v => [0 1 2 3]))

  ;; Grid Test
  (let [v (atom [])
        s (d/->Grid 3 3 (into {} (for [x (range 3)
                                       y (range 3)]
                                   [[x y] (->Insert [x y] v)])))]
    (d/drawable->sketch s)
    (fact "all tiles are drawn"
          @v => (just (for  [x (range 3)
                             y (range 3)]
                        [x y]))))
  (let [v (atom [])
        s (d/->DerefMiddleware (atom (->Insert :dereffed v)))]
    (d/drawable->sketch s)
    (fact "The wrapped drawable is drawn"
          @v => [:dereffed]))
  
  ;; Deref test
  )

(fact "drawable? on a non-drawable returns false."
  (d/drawable? 42) => FALSEY)

(fact "drawable? on a drawable returns true."
  (d/drawable? (d/->Nothing)) => TRUTHY)
