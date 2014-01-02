(ns partial-fn.util-test
  (:use [midje.sweet]
        [partial-fn.util]))

(facts "about `make-keyword-map`"
       (let [x 3
             y 4
             z :zeta]
         (make-keyword-map x y z)) => {:x 3 :y 4 :z :zeta})

