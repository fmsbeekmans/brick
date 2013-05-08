(ns brick.core
  (:use [quil.core :exclude [size]])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [brick.debug :as debug])
  (:import [brick.layer GridLayer]))

(declare brick-load)

(def
  #^{:doc "The current application"
     :dynamic true}
  bricklett nil)

(defn start
  "Launch a test application"
  []
  (brick-load
   "Tyler"
   [640 320]
   #()
   (atom [])
   (atom []) nil))

(defn- setup
  "Prepare the engine"
  []
  {:pre [bricklett]}
  (swap! (:tiles bricklett)
         (fn [tiles]
           (concat tiles
                   (tile/load-tiles
                    (load-image "resources/tiles2.png") 32))))
    (smooth))

(defn title

  "The title of the running application"
  []
  {:pre [bricklett]}
  (:title bricklett))

(defn size
  "The size of the running application"
  []
  {:pre [bricklett]}
  (:size bricklett))

(defn layers
  "The layers the engine will render"
  []
  {:pre [bricklett]}
  (:layers bricklett))

(defn tiles
  "The tiles the game tile layers can use"
  []
  {:pre [bricklett]}
  (:tiles bricklett))

(defn- render
;  "Actually render the layers."
  []
  {:pre [bricklett]}
  (map #(.draw %) (layers)))

(defn- update
  "Clock tick"
  []
  {:pre [bricklett]}
  (tile/with-tiles (vec @(:tiles bricklett))
    (tile/with-dictionary {:paths {:road 0
                                   :canal 3
                                   :railroad 5}
                             :b 1
                             :c 2}
      ((tile/tile :paths :canal) 32))))

(defmacro brick-load
  "Start a brick application"
  [& args]
  {:pre [(nil? bricklett)]}
  `(binding [bricklett (apply hash-map ~(vec args))])
  `(defsketch ~(title)
     :title ~(title)
     :setup ~(setup)
     :draw ~(update)
     :size ~(size)))
