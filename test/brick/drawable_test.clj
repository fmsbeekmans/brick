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
                              ((arg-map :setup) nil)
                                => (throws clojure.lang.ArityException)
                              ((arg-map :setup)))
                             (fact
                              "Contains draw fn"
                              arg-map
                                => (contains {:draw fn?})
                              ((arg-map :draw) nil)
                                => (throws clojure.lang.ArityException))))]
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
  (d/drawable->sketch
   (d/->Bricklet (atom [])
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

(fact "image"
  (with-redefs [quil.core/width (fn [] 100)
                quil.core/height (fn [] 100)
                quil.core/image (fn [img x y h w]
                                  (fact "Image gets correct paramers"
                                    img => :img
                                    x => 0
                                    y => 0
                                    h => 100
                                    w => 100))
                quil.core/sketch (fn [& args]
                                   (let [arg-map (named-args args)]
                                     ((:draw arg-map))))]
    (d/drawable->sketch (d/->Image :img))))

(fact "Floating"

 (let [translates (atom [])
       scales (atom [])]
   (with-redefs [quil.core/width (fn [] 100)
                 quil.core/height (fn [] 100)
                 quil.core/with-translation (fn
                                              #^{:macro true}
                                              [translation _]
                                              (swap! translates conj translation))
                 quil.core/with-rotation (fn
                                           #^{:macro true}
                                           [& rotation]
                                           (fact "Correct rotation parameters"
                                             rotation => '([2] [[-50 -50]])))
                 brick.util/with-scale (fn
                                         #^{:macro true}
                                         [& _]
                                         (println "scale" _))
                 quil.core/image (fn [img x y h w]
                                   (fact "Image gets correct paramers"
                                     img => :img
                                     x => 0
                                     y => 0
                                     h => 100
                                     w => 100))
                 quil.core/push-matrix (fn [])
                 quil.core/pop-matrix (fn [])
                 quil.core/translate (fn [& _])
                 quil.core/scale (fn [& _])
                 quil.core/rotate (fn [& _])
                 quil.core/sketch (fn [& args]
                                    (let [arg-map (named-args args)]
                                      ((:draw arg-map))))]
     (d/drawable->sketch (d/->Floating (d/->Image :img) [0.5 0.1] 0.01 2))
     @translates => [[-50 -50]
                    [50 50]
                    [0.0 -0.4]])))

(fact "Derefmiddleware")

(fact "Execution queue facts."
  (with-redefs [quil.core/width (fn [] 100)
                quil.core/height (fn [] 100)
                quil.core/sketch (fn [& args]
                                   (let [arg-map (named-args args)]
                                     (defn step []
                                       ((:draw arg-map)))))]
    (let [queue (atom [])
          sink (atom [])
          br (d/->Bricklet (atom (->Insert :draw sink))
                           queue)]
      (fact "Execution queue is processed after every draw.")
      (d/drawable->sketch br)
      (swap! queue conj (fn [_]
                          (swap! sink conj :first-command)))
      (step)
      (swap! queue conj (fn [_]
                          (swap! sink conj :second-command)))
      (step)
      @sink => [:draw :first-command :draw :second-command])))


(fact "drawable? on a non-drawable returns false."
  (d/drawable? 42) => FALSEY)

(fact "drawable? on a drawable returns true."
  (d/drawable? (d/->Nothing)) => TRUTHY)
