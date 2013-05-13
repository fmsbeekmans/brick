(ns brick.core
  (:use [quil.core :exclude [size]]
        brick.drawable)
  (:require [brick.image :as image]
            quil.applet)
  (:import [brick.drawable Bricklet]
           [brick.drawable Image]
           [brick.drawable Grid]
           [brick.drawable Stack])
  (:gen-class))

(defmacro defbricklet
  "Create a new bricklet"
  [layers & args]
  `(let [bricklet# (Bricklet. ~layers)]
     (assoc bricklet# ~@args)))

(defmacro brick-sketch
  "Start sketching a bricklet."
  [sym bricklet]
  `(defsketch ~sym
     :draw (fn []
             (.draw ~bricklet [(width) (height)]))
     :setup #((var setup) ~bricklet)
     :size (or (:size ~bricklet)
               [200 200])
     :title (or (:title ~bricklet)
                "")))

(defn- setup
  "Prepare an bricklet. This includes initializing tiles and layers."
  [bricklet]
  (frame-rate 1)
  (swap!
   (:images bricklet)
   (fn [old]
     (concat old
             (image/load-images (load-image "resources/32x32.png") 32))))
  ((:layers-init bricklet) bricklet))

(defn scedule [bricklet command]
  (swap! (:execute-que bricklet) conj command))
