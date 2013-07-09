(ns brick.drawable
  "Everything that can be drawn and their helper functions."
  (:use [brick.util :only [with-scale]])
  (:require [quil.core :as q]))

(def min-borders [0.1 0.1])

(defn ranges
  "Return a list of [offset size] so that pixels is divided
into n pieces."
  ([n pixels offset]
     (let [size (/ pixels n)]
       (vec
        (for [i (range n)]
          (let [start (int (Math/ceil (* i size)))
                 end (int (Math/ceil (* (inc i) size)))]
            [(+ offset start)
             (- end start)])))))
  ([n pixels]
     (ranges n pixels 0)))

(defprotocol Drawable
  "Anything that can be drawn"
  (draw [this [w h]]
    "Draw the given object w times h pixels large."))

(defn drawable?
  "Returns whether or not the given argument is a drawable."
  [x]
  (extends? Drawable (type x)))

(defrecord Image
    #^{:doc "A drawable wrapper around a PImage. "}
  [^processing.core.PImage img]
  Drawable
  (draw [this [w h]]
    (q/image (:img this) 0 0 w h)))

(defn *-pi "take a number, multiply it by pi."
  [n]
  (* Math/PI n))

(defrecord Floating
    #^{:doc "A wrapper for drawables to float a drawable."}
  [drawable center-scales scale rotation]
  Drawable
  (draw [this [w h]]
    (q/with-translation (vec (map (fn [scale p]
                                    (int (* scale p)))
                                  (:center-scales this)
                                  [w h]))
      (q/with-rotation [(:rotation this)]
        (with-scale [(:scale this)]
          (q/with-translation [(- (* w 0.5))
                               (- (* h 0.5))]
            (.draw ^brick.drawable.Drawable (:drawable this) [w h])))))))

(defrecord Stack [layers]
  #^{:doc "A stack of drawables on top of one another."}
  Drawable
  (draw [this [w h]]
    (doseq [layer (:layers this)]
      (.draw ^brick.drawable.Drawable layer [w h]))))

(defrecord Grid [w h grid]
  #^{:doc "A grid of drawables exactly side by side."}
  Drawable
  (draw [this [w h]]
    (let [h-ranges (ranges (:w this) w)
          v-ranges (ranges (:h this) h)]
      (doseq [x (range (:w this))
              y (range (:h this))]
        (q/with-translation [(get-in h-ranges [x 0])
                           (get-in v-ranges [y 0])]
          (.draw ^brick.drawable.Drawable ((:grid this) [x y])
                 [(get-in h-ranges [x 1])
                  (get-in v-ranges [y 1])]))))))

(defrecord Nothing []
  #^{:doc "A placeholder for an empty (/transparant) space."}
  Drawable
  (draw [_ _]))

(defrecord Bricklet
    [target-drawable command-queue]
  #^{:doc "A special stacklayer."}
  Drawable
  (draw [this [w h]]
    (.draw ^brick.drawable.Drawable @(:target-drawable this) [w h])
    (doseq [command @command-queue]
      (command this))
    (reset! command-queue [])))

(defn ->Bricklet
  "Create a new bricklet with layers, exec-queue and opts.
use :init for setup in graphics environment.
:draw will be overridden in drawable->sketch."
  [target command-queue & opts]
  (let [br (Bricklet. target command-queue)
        opts-map (apply hash-map opts)
        params {:size [100 100]
                :framerate 10
                :title "No title"
                :images (atom [])}
        with-setup (merge params {:setup (:init params)})]
    (apply (partial assoc br) (apply concat (merge params opts-map br)))))

(defrecord DerefMiddleware
  [target-drawable]
  Drawable
  (draw [this [w h]]
    (.draw ^brick.drawable.Drawable @(:target-drawable this) [w h])))

(defn drawable->sketch!
  "Creates a sketch from a bricklet and quil options"
  [drawable]
  (apply q/sketch (apply concat
                       (assoc drawable
                         :setup (partial (or (:init drawable)
                                             (fn [_])) drawable)
                         :draw (fn []
                                 ;(q/background 255 255 255)
                                 (.draw ^brick.drawable.Drawable drawable [(q/width) (q/height)]))))))

(defrecord Border
  [target border-w border-h]
  Drawable
  (draw
    [this [w h]]
    (let [border-w' (int (* (:border-w this) w))
          border-h' (int (* (:border-h this) h))
          w' (- w border-w' border-w')
          h' (- h border-h' border-h')]
      (q/with-translation [border-w' border-h']
        (.draw ^brick.drawable.Drawable (:target this) [w' h'])))))

(defn square-borders-size
  ([[screen-w screen-h] [w h]]
     (square-borders-size [screen-w screen-h] [w h] [0 0]))
  ([[screen-w screen-h] [w h] [min-border-w min-border-h]]
     (let [screen-w' (- screen-w (* 0.5 min-border-w screen-w))
           screen-h' (- screen-h (* 0.5 min-border-h screen-h))
           w' (quot screen-w' w)
           h' (quot screen-h' h)
           d (min h' w')]
       [(/ (* 0.5 (- screen-w (* w d)))
           screen-w)
        (/ (* 0.5 (- screen-h (* h d)))
           screen-h)])))

(defrecord SquareTiledGrid
  [w h grid min-border-w min-border-h]
  Drawable
  (draw [this [w h]]
    (.draw ^brick.drawable.Drawable
     (apply ->Border
            (->Grid (:w this) (:h this) (:grid this))
            (square-borders-size
             [(q/width) (q/height)]
             [(:w this) (:h this)]
             [(:min-border-w this)
              (:min-border-h this)]))
     [w h])))
