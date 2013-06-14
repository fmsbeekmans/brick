(ns brick.examples.simple
  "barebones example."
  (:use [quil.core :exclude [size]])
  (:require [brick.drawable :as drawable]
            [brick.image :as image])
  (:gen-class))

;;; These Vars should be abstracted by let bindings in production usage
(def images (atom []))
(def layers (atom []))
(def commands (atom []))
(def dict {:bricks 1 :bush-l 4 :bush-r 5})

(defn- images-init [old]
  "Prepare the images used in the sketch."
  (vec (concat old
               (image/load-images (load-image "resources/32x32.png") [32 32]))))

(defn- target-init [old]
  "The target of the bricklet, initialize what will be shown."
  (let [lookup #(@images (or (dict %)
                             %))]
    (drawable/->Image (load-image "colors.png"))))

(defn- init
  "Prepare a bricklet. This includes initializing tiles and layers."
  [bricklet]
  (frame-rate 2)
  (background 0)
  (swap! images images-init)
  (swap! (:target-drawable bricklet) target-init))

(defn color-bg [bricklet]
  (swap! (:command-queue bricklet) conj (fn [_] (background 50 50 100))))

;een init maken voor de target-drawable, kan alleen Nothing en
;composieties daarvan initializeren

(defn -main [& args]

  (def br (drawable/->Bricklet layers commands
                               :init init
                               :size [500 500]
                               :title "Let there be title!"))
  (drawable/drawable->sketch! br))
