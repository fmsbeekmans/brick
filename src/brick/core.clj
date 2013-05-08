(ns brick.core
  (:use [quil.core :exclude [size]])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [brick.debug :as debug])
  (:import [brick.layer GridLayer]))

(declare brick-load)
(declare update!)
(declare setup)

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
  (let [dbg (debug/simple-dbg)
        bricklett (apply hash-map opts)]
    (defsketch sym
      :setup (fn []
               (binding [bricklett bricklett]
                 (debug/toggle dbg)
                 (debug/add-line dbg "init-args" (atom bricklett))))
      :title (:title bricklett)
      :draw #(binding [bricklett bricklett]
               (debug/draw dbg))
      :size (:size bricklett))))

(defn- init-tiles!
  "initialize tiles"
  [source-image]
  {:pre [(active?)]}
  (swap! (:tiles bricklett)
         (fn [tiles]
           (concat tiles
                   (tile/load-tiles
                    (load-image "resources/tiles2.png") 32)))))

(defn setup
  "Prepare the engine"
  []
  {:pre [(active?)]}
  (background 0))

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

  (comment
    (tile/with-tiles (vec @(:tiles bricklett))
      (map (fn [layer]
             (tile/with-dictionary (:dictionary layer)
               (.draw layer)))
           layers))))

(defn- update!
  "Clock tick"
  []
  {:pre [(active?)]}
  (render!)
  ;(map #(.update %) (:layers bricklett))
  )

(defmacro brick-load
  "Start a brick application"
  [& args]
  `(defbrick '~(symbol (:title (apply hash-map args))) ~@args))

(defn start!
  "Launch a test application"
  []
  (brick-load
   :title "Tyler"
   :size [640 320]
   :tiles (atom [])
   :layers (atom "aoe")))
