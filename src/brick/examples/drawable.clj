(ns brick.examples.drawable
  "example demonstrating most drawable's."
  (:use [quil.core :exclude [size]])
  (:require [brick.drawable :as drawable]
            [brick.image :as image])
  (:gen-class))

;;; These Vars should be abstracted by let bindings in production usage
(def images
  "A repository of loaded images." (atom []))
(def layers
  "The Drawable object that will be sketched." (atom []))
(def commands
  "A queue. Write 1-arity fn's here, param will be the drawable.
Command will execute next draw cycle." (atom []))
(def dict
  "A lookup table for images." {:bricks 1 :bush-l 4 :bush-r 5})

(defn- images-init
  "Load the images that will be needed."
  [old]
  (vec (concat old
               (image/load-images (load-image "resources/32x32.png") [32 32]))))

(defn- target-init
  "Initialize a composition of drawables."
  [_]
  (let [lookup #(@images (or (dict %)
                             %))]
    (drawable/->Grid
     2
     1
     {[0 0] (drawable/->Image (load-image "colors.png"))
      [1 0] (drawable/->Stack [(lookup :bush-l)
                               (drawable/->Grid 1 2 {[0 0] (drawable/->Nothing)
                                                     [0 1] (lookup 7)})])})))

(defn- init
  "Prepare a bricklet. This includes initializing tiles and layers."
  [bricklet]
  (frame-rate 2)
  (background 0)
  (swap! images images-init)
  (swap! (:target-drawable bricklet) target-init))

(defn -main
  "Start sketching!"
  [& _]
  (let [br (drawable/->Bricklet layers commands
                                 :init init
                                 :size [500 500]
                                 :title "Let there be title!")]
    (drawable/drawable->sketch! br)))
