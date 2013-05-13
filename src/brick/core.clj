(ns brick.core
  (:use [quil.core :exclude [size]])
  (:require [brick.image :as image]
            [brick.debug :as debug]
            [brick.drawable :as drawable]
            [quil.applet :as app])
  (:import [brick.drawable Bricklet]
           [brick.drawable Image]
           [brick.drawable Grid])
  (:gen-class))

(defmacro defbricklet
  [layers & args]
  `(let [bricklet# (drawable/Bricklet. ~layers)]
     (assoc bricklet# ~@args)))

(defmacro brick-sketch
  [sym bricklet]
  `(defsketch ~sym
     :draw (fn []
             (.draw ~bricklet [(width) (height)]))
     :setup #((var setup) ~bricklet)
     :size (or (:size ~bricklet)
               [200 200])
     :title (or (:title ~bricklet)
                "")))

(defn setup [bricklet]
  (swap!
   (:images bricklet)
   (fn [old]
     (concat old
             (image/load-images (load-image "resources/32x32.png") 32))))
  ((:layers-init bricklet) bricklet))

(def br (defbricklet
          (atom [])
          :layers-init (fn [bricklet]
                         (swap! (:layers bricklet)
                                (fn [old]
                                  [(drawable/Grid. 3 2 {[0 0] (image/get-image-in bricklet :bush-l)
                                                        [1 0] (image/get-image-in bricklet :bush-r)
                                                        [2 0] (image/get-image-in bricklet 6)
                                                        [0 1] (image/get-image-in bricklet :bush-r)
                                                        [1 1] (image/get-image-in bricklet :bush-l)
                                                        [2 1] (image/get-image-in bricklet :bricks)})])))
          :dictionary {:bricks 1
                       :bush-l 4
                       :bush-r 5}
          :images (atom [])
          :size [500 500]))

(defn -main [& args]
  (brick-sketch a br))

(comment [(drawable/GridLayer. 2 1 {[0 0] (@(:images bricklet) 2)
                                    [1 0] (@(:images bricklet) 3)})])
