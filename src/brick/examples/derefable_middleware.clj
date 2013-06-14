(ns brick.examples.derefable-middleware
  "Example demonstrating a wrapper."
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

(def swap-img
  "This image will be replaced. Old image will remain
visible unless it is completely painted over." (atom []))
(def proxy-middleware
  "The wrapper that allows the image to be swopped."
  (atom []))

(defn- images-init
  "Load the images that are used in the draw function."
  [old]
  (vec (concat old
               (image/load-images
                (load-image "resources/32x32.png") [32 32]))))

(defn- target-init
  "Init swapable drawthingy!"
  [_]
  (let [lookup #(@images (or (dict %)
                             %))]
    (swap! proxy-middleware (fn [_]
                              (drawable/->DerefMiddleware
                               (atom (lookup 7)))))
    @proxy-middleware))

;; All the resources need to be loaded in a quil environment.
;; This can be done by attaching an init k-v pair.

(defn- init
  "Prepare a bricklet. This includes initializing tiles and layers."
  [bricklet]
  (frame-rate 2)
  (background 0)
  (swap! images images-init)
  (swap! (:target-drawable bricklet) target-init)
  (swap! swap-img (fn [_]
                    (drawable/->Image
                     (load-image "resources/32x32.png")))))

(defn -main
  "Launch the sketch"
   [& _]
  (let [br (drawable/->Bricklet (atom layers) commands
                             :init init ;point to the init fn before
                                        ;the drawing starts.
                             :size [500 500]
                             :title "Let there be title!")
        br-sketch (drawable/drawable->sketch! br)]
    (Thread/sleep 2000)
    (swap! commands conj
           (fn [_]
             (swap! (:target-drawable @proxy-middleware)
                    conj @swap-img)))))
