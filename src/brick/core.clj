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

(defn active?
  "Is there a running ap?"
  []
  bricklett)

(defn defbrick
  "Create a new brick"
  [sym & opts]
  {:pre [(not (active?))]}
  (println opts)
  (binding [bricklett (apply hash-map opts)]
    (defsketch sym
      :setup (:setup bricklett)
      :title (:title bricklett)
      :draw (:update bricklett)
      :size (:size bricklett))))

(defn start!
  "Launch a test application"
  []
  (brick-load
   :title "Tyler"
   :size [640 320]
   :setup #()
   :tiles (atom [])
   :init-tiles #()
   :layers (atom [])
   :draw #()))

(defn- init-tiles!
  "initialize tiles"
  [source-image]
  {:pre [(active?)]}
  (swap! (:tiles bricklett)
         (fn [tiles]
           (concat tiles
                   (tile/load-tiles
                    (load-image "resources/tiles2.png") 32)))))

(defn- setup!
  "Prepare the engine"
  []
  {:pre [(active?)]}
  (smooth)
  (init-tiles!))

(defn title
  "The title of the running application"
  []
  {:pre [(active?)]}
  (:title bricklett))

(defn size
  "The size of the running application"
  []
  {:pre [(active?)]}
  (:size bricklett))

(defn layers
  "The layers the engine will render"
  []
  {:pre [(active?)]}
  (:layers bricklett))

(defn tiles
  "The tiles the game tile layers can use"
  []
  {:pre [(active?)]}
  (:tiles bricklett))

(defn- render!
  "Actually render the layers."
  []
  {:pre [(active?)]}
  (tile/with-tiles (vec @(:tiles bricklett))
    (map (fn [layer]
           (tile/with-dictionary (:dictionary layer)
             (.draw layer)))
         layers)))

(defn- update!
  "Clock tick"
  []
  {:pre [(active?)]}
  (render!)
  (map #(.update %) (:layers bricklett)))

(defmacro brick-load
  "Start a brick application"
  [& args]
  `(defbrick '~(symbol (:title (apply hash-map args))) ~@args))
