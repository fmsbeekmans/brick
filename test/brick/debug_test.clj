(ns brick.debug-test
  (:use midje.sweet
        brick.util)
  (:require [brick.debug :as d]
            [brick.drawable :as dr]))

(let [dbg (d/simple-dbg)]
  (with-redefs [quil.core/width (fn []
                                  100)
                quil.core/height (fn []
                                   100)
                quil.core/push-style (fn [])
                quil.core/pop-style (fn [])
                quil.core/fill-int (fn [& _])
                quil.core/stroke-int (fn [& _])
                quil.core/rect (fn [& _])
                quil.core/text (fn [text & _]
                                 (fact "The label is printed."
                                   (set (re-seq #"label" text))
                                     => #{"label"})
                                 (fact "The dereffed object is printed."
                                   (set (re-seq #"[0-9]" text))
                                     => #{"1"}))
                quil.core/sketch (fn [& args]
                                   (let [arg-map (named-args args)]
                                     ((:draw arg-map))))]
    (let [dbg (d/simple-dbg)]
      
      (fact "add-line, line gets "
            (let [i (atom 0)
                  up (fn []
                       (swap! i inc)
                       (dec @i))
                  sink (atom [])
                  line (proxy [clojure.lang.IDeref] []
                         (deref []
                           (swap! sink conj (up))
                           @i))]
                                        ;sketch maken, draw invoken.
              (.toggle dbg)
              (.add-line dbg "label" line)
              (dr/drawable->sketch! (dr/->DerefMiddleware (atom dbg)))
              @i => 1))
      (fact "remove-line, line gets "
            (let [i (atom 0)
                  up (fn []
                       (swap! i inc)
                       (dec @i))
                  sink (atom [])
                  line (proxy [clojure.lang.IDeref] []
                         (deref []
                           (swap! sink conj (up))
                           @i))]
                                        ;sketch maken, draw invoken.
              (.toggle dbg)
              (.remove-line dbg line)
              (dr/drawable->sketch! (dr/->DerefMiddleware (atom dbg)))
              @i => 0))
      (fact "Toggle?"
                   (let [switch (.toggle dbg)
                         switch-back (.toggle dbg)]
                     switch => true
                     switch-back => false)))))
