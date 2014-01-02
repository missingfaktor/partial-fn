(ns partial-fn.core-test
  (:use [clojure.test]
        [partial-fn.core]
        [midje.sweet]))

(fact "make-keyword-map creates a keyword map"
      (let [x 3
            y 4
            z :zeta]
        (make-keyword-map x y z)) => {:x 3 :y 4 :z :zeta})

; (use '[clojure.core.match :only (match)]) (use 'partial-fn.core)