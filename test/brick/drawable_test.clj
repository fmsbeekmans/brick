(ns brick.drawable-test
  (:use midje.sweet)
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
        (d/drawable->sketch! (d/->Nothing))))

(fact "drawable->sketch uses :init"
      (with-redefs [q/sketch (fn [& args]
                               (let [arg-map (named-args args)]
                                 ((:init arg-map))))]
        (d/drawable->sketch! (d/->Bricklet (atom [])
                                          (atom [])
                                          :init (fn []
                                                  anything => anything)))))

(with-redefs [q/sketch (fn [& args]
                         (let [arg-map (named-args args)]
                           ((:init arg-map))))]
  (d/drawable->sketch!
   (d/->Bricklet (atom [])
                 (atom [])
                 :init (fn []
                         (fact "drawable->sketch uses :init"
                               anything => anything))
                 :draw (fn []
                         (fact "drawable->sketch overrides :draw"
                               true => falsey)))))

(fact "number to radials"
  (d/*-pi 1) => Math/PI
  (d/*-pi 2) => (* 2 Math/PI))

(defrecord Insert [insert into]
    Drawable
    (draw [this [w h]]
      (swap! (:into this) (fn [old]
                            (conj old (:insert this))))))

(fact "image"
  (with-redefs [quil.core/width (fn []
                                  100)
                quil.core/height (fn []
                                   100)
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
    (d/drawable->sketch! (d/->Image :img))))

(fact "Floating"
 (let [translates (atom [])
       scales (atom [])]
   (with-redefs [quil.core/width (fn []
                                   100)
                 quil.core/height (fn []
                                    100)
                 quil.core/push-matrix (fn [])
                 quil.core/pop-matrix (fn [])
                 brick.util/with-scale (fn
                                         #^{:macro true}
                                         [scale _]
                                         scale => 0.01)
                 quil.core/image (fn [img x y h w]
                                   (fact "Image gets correct paramers"
                                     img => :img
                                     x => 0
                                     y => 0
                                     h => 100
                                     w => 100))
                 quil.core/translate (fn [translation]
                                       (swap! translates conj translation))
                 quil.core/scale (fn [s]
                                   (fact "Image is drawn in the correct scale"
                                     s => 0.01))
                 quil.core/rotate (fn [r]
                                    (fact "Correct rotation parameters"
                                      r => 2))
                 quil.core/sketch (fn [& args]
                                    (let [arg-map (named-args args)]
                                      ((:draw arg-map))))]
     (d/drawable->sketch! (d/->Floating (d/->Image :img) [0.5 0.1] 0.01 2))
     @translates => [[0.0 -0.4]
                    [50 50]
                    [-50 -50]])))

(fact "Derefmiddleware"
  (let [sink (atom [])]
    (with-redefs [quil.core/width (fn []
                                    100)
                  quil.core/height (fn [])
                  quil.core/sketch (fn [& args]
                                     (let [arg-map (named-args args)]
                                       ((:draw arg-map))))]
      (d/drawable->sketch! (d/->DerefMiddleware (atom (->Insert :target sink))))
      @sink => [:target])))

(fact "Stack"
  (let [sink (atom [])]
    (with-redefs [quil.core/width (fn []
                                    100)
                  quil.core/height (fn [])
                  quil.core/sketch (fn [& args]
                                     (let [arg-map (named-args args)]
                                       ((:draw arg-map))))]
      (d/drawable->sketch! (d/->Stack [(->Insert :first sink)
                                    (->Insert :second sink)]))
      @sink => [:first :second])))

(fact "Grid"
  (let [sink (atom [])
        translations (atom [])]
    (with-redefs
      [quil.core/width (fn []
                         100)
       quil.core/height (fn []
                          100)
       quil.core/push-matrix (fn [])
       quil.core/pop-matrix (fn [])
       quil.core/translate (fn [translate]
                             (fact "Pieces are translated to the correct point."
                               (swap! translations conj translate)))

       quil.core/sketch (fn [& args]
                          (let [arg-map (named-args args)]
                            ((:draw arg-map))))]
      (d/drawable->sketch! (d/->Grid 2 2 {[0 0] (->Insert [0 0] sink)
                                          [0 1] (->Insert [0 1] sink)
                                          [1 0] (->Insert [1 0] sink)
                                          [1 1] (->Insert [1 1] sink)}))
      @sink => [[0 0]
                [0 1]
                [1 0]
                [1 1]]
      @translations => [[0 0]
                        [0 50]
                        [50 0]
                        [50 50]])))

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
      (d/drawable->sketch! br)
      (swap! queue conj (fn [_]
                          (swap! sink conj :first-command)))
      (step)
      (swap! queue conj (fn [_]
                          (swap! sink conj :second-command)))
      (step)
      @sink => [:draw :first-command :draw :second-command])))

(fact "Bricklet draws"
  (with-redefs [quil.core/width (fn [] 100)
                quil.core/height (fn [] 100)
                quil.core/sketch (fn [& args]
                                   (let [arg-map (named-args args)]
                                     ((:draw arg-map))))]))

(fact "drawable? on a non-drawable returns false."
  (d/drawable? 42) => FALSEY)

(fact "drawable? on a drawable returns true."
  (d/drawable? (d/->Nothing)) => TRUTHY)
