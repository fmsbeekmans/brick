(ns brick.image-test
  (:use midje.sweet)
  (:use [brick.image]))

(facts "load-images."
  (fact "load all the appropriate images.")
  (fact "returns an empty vector when no images are available."))

(facts "new-p-image."
  (fact "create the correct image.")
  (fact "sketch unaffected."))

(facts "append-images!"
  (fact "appropriate file gets rewritten.")
  (fact "new file for non-existing strip."))
