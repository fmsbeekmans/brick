(ns brick.examples.drawable
  (:use [quil.core :exclude [size]]
        brick.core)
  (:require [brick.drawable :as drawable]
            [brick.image :as image])
  (:gen-class))

;;; These Vars should be abstracted by let bindings in production usage
(def images (atom []))
(def layers (atom []))
(def commands (atom []))
(def dict {:bricks 1 :bush-l 4 :bush-r 5})

(defn- images-init [old]
  (vec (concat old
               (image/load-images (load-image "resources/32x32.png") [32 32]))))

(defn- layers-init [old]
  (let [lookup #(@images (image/dictionary dict %))]
    (drawable/->Grid 2 1
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
  (swap! (:target-drawable bricklet) layers-init))

(defn color-bg [bricklet]
  (swap! (:command-queue bricklet) conj (fn [_] (background 50 50 100))))

;een init maken voor de target-drawable, kan alleen Nothing en
;composieties daarvan initializeren

(defn -main [& args]
  (def br (drawable/->Bricklet layers commands
                               :init init
                               :size [500 500]
                               :title "Let there be title!"))
  (bricklet-sketch br))
