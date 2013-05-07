(ns brick.core
  (:use [quil.core])
  (:require [brick.tile :as tile]
            [brick.layer :as layer]
            [clojure.java.io :as io])
  (:import [brick.layer GridLayer])
  (:gen-class))

(defn setup [tiles-atom]
  (smooth))

(def t (atom 0))

(def arghatom nil)

(defn draw [tiles-atom]
  (frame-rate 10)
  (tile/load)
  (with-translation [(/ (width) 2) (/ (height) 2) ]
    (with-rotation [@t]
      (with-translation [(+ 10 @t) 0]
        (image (nth @tiles-atom 7) 0 0 @t @t))))
  (swap! t inc))

(defn start []
  (let [tiles (atom [])]
    (defsketch example
      :title "Tiles!"
      :setup #(setup tiles)
      :draw #((var draw) tiles)
      :size [320 320])))
