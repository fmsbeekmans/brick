(ns brick.core
  (:use [quil.core :exclude [size]])
  (:require [brick.image :as image]
            [brick.debug :as debug]
            [brick.drawable :as drawable]
            [quil.applet :as app])
  (:import [brick.drawable Bricklet]
           [brick.drawable Image]
           [brick.drawable Grid]
           [brick.drawable Stack])
  (:gen-class))

(defmacro defbricklet
  "Create a new bricklet"
  [layers & args]
  `(let [bricklet# (drawable/Bricklet. ~layers)]
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

(defn setup
  "Prepare an bricklet. This includes initializing tiles and layers."
  [bricklet]
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
                                  [(drawable/Grid. 3 2
                                                   {[0 0] (drawable/Stack. [(image/get-image-in bricklet :bricks)
                                                                                 (image/get-image-in bricklet :bush-l)])
                                                    [1 0] (image/get-image-in bricklet :bush-r)
                                                    [2 0] (image/get-image-in bricklet 6)
                                                    [0 1] (image/get-image-in bricklet :bush-r)
                                                    [1 1] (drawable/Grid. 3 2
                                                                          {[0 0] (drawable/Image. (image/new-p-image [10 10]
                                                                                                                     (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))))
                                                                           [1 0] (drawable/Image. (image/new-p-image [10 10]
                                                                                                                     (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))                                                                                                                     ))
                                                                           [2 0] (drawable/Image. (image/new-p-image [10 10]
                                                                                                                     (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))
                                                                                                                     ))
                                                                           [0 1] (drawable/Image. (image/new-p-image [10 10]
                                                                                                                     (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))
                                                                                                                     ))
                                                                           [1 1] (drawable/Image. (image/new-p-image [10 10]
                                                                                                                     (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))
                                                                                                                     ))
                                                                           [2 1] (drawable/Image. (image/new-p-image [10 10]
                                                                                                                     (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))
                                                                                                                     ))})
                                                    [2 1] (drawable/Image. (image/new-p-image [10 10]
                                                                                              (background (rand-int 256)
                                                                                                                                 (rand-int 256)
                                                                                                                                 (rand-int 256))
                                                                                                                     ))})])))
          :dictionary {:bricks 1
                       :bush-l 4
                       :bush-r 5}
          :images (atom [])
          :size [500 500]))

(defn -main [& args]
  (brick-sketch a br))

