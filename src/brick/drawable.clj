(ns brick.drawable
  "Everything that can be drawn and their helper functions."
  (:use quil.core
        brick.util))

(defn- ranges [n pixels]
  "Return a list of [offset size] so that pixels is divided into n pieces."
  (let [size (/ pixels n)]
    (vec
     (for [i (range n)]
       (let [start (int (Math/ceil (* i size)))
             end (int (Math/ceil (* (inc i) size)))]
         [start (- end start)])))))

(defprotocol Drawable
  "Anything that can be drawn"
  (draw [this [w h]]
    "Draw the given object w times h pixels large."))

(defn drawable?
  "Returns whether or not the given argument is a drawable."
  [x]
  (extends? Drawable (type x)))

(defrecord Image
    #^{:doc "A drawable wrapper around a PImage. "}
  [img]
  Drawable
  (draw [this [w h]]
    (image (:img this) 0 0 w h)))

(defn *-pi [n]
  ""
  (* Math/PI n))

(defrecord Floating
    #^{:doc "A wrapper for drawables to float a drawable."}
  [drawable center-scales scale rotation]
  Drawable
  (draw [this [w h]]
    (with-translation (vec (map (fn [center-scale p]
                                  (+
                                   (- (* 0.5 (:scale this) p ))
                                   (* scale center-scale p)))
                                (:center-scales this)
                                [w h]))
      (with-translation [(/ w 2) (/ h 2)]
        (with-scale [(:scale this)]
          (with-rotation [(:rotation this)]
            (with-translation [(- (/ w 2)) (- (/ h 2))]
              (.draw (:drawable this) [w h]))))))))

(defrecord Stack [layers]
  #^{:doc "A stack of drawables on top of one another."}
  Drawable
  (draw [this [w h]]
    (doseq [layer (:layers this)]
      (.draw layer [w h]))))

(defrecord Grid [w h grid]
  #^{:doc "A grid of drawables exactly side by side."}
  Drawable
  (draw [this [w h]]
    (let [h-ranges (ranges (:w this) w)
          v-ranges (ranges (:h this) h)]
      (doseq [x (range (:w this))
              y (range (:h this))]
        (with-translation [(get-in h-ranges [x 0])
                           (get-in v-ranges [y 0])]
          (.draw ((:grid this) [x y])
                 [(get-in h-ranges [x 1])
                  (get-in v-ranges [y 1])]))))))

(defrecord Nothing []
  #^{:doc "A placeholder for an empty (/transparant) space."}
  Drawable
  (draw [_ _]))

(defrecord Bricklet [target-drawable command-queue]
  #^{:doc "A special stacklayer."}
  Drawable
  (draw [this [w h]]
    (.draw @target-drawable [w h])
    (doseq [command @command-queue]
      (command this))
    (reset! command-queue [])))

(defn ->Bricklet
  #^{:doc (str "Create a new bricklet with layers, exec-queue and opts.\n"
               "use :init for setup in graphics environment.\n"
               ":draw will be overridden in drawable->sketch.")}
  [target command-queue & opts]
  (let [br (Bricklet. target command-queue)
        opts-map (apply hash-map opts)
        params {:size [100 100]
                :title "No title"
                :images (atom [])}
        with-setup (merge params {:setup (:init params)})]
    (apply (partial assoc br) (apply concat (merge params opts-map br)))))

(defrecord DerefMiddleware [target-drawable]
  Drawable
  (draw [this [w h]]
    (.draw @(:target-drawable this) [w h])))

(defn drawable->sketch!
  "Creates a sketch from a bricklet and quil options"
  [bricklet]
  (apply sketch (apply concat
                       (assoc bricklet
                         :setup #((or (:init bricklet)
                                      (fn [b])) bricklet)
                         :draw (fn []
                                 (.draw bricklet [(width) (height)]))))))
