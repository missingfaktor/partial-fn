(ns partial-fn.core-test
  (:use [clojure.test]
        [partial-fn.core]
        [midje.sweet]))

(facts "about `make-keyword-map`"
       (let [x 3
             y 4
             z :zeta]
         (make-keyword-map x y z)) => {:x 3 :y 4 :z :zeta})

(def matcher-1 [[1 2] "x"])
(def matcher-2 [[2 3] "y"])
(def matcher-3 [:else "z"])

(facts "about `separate-matchers`"
       (separate-matchers [matcher-1 matcher-2 matcher-3]) => [[matcher-1 matcher-2] "z"]
       (separate-matchers [matcher-1 matcher-2]) => [[matcher-1 matcher-2] nil])

(facts "about `match-fn-block->node`"
       (match-fn-block->node '(fn [x y]
                                (match [x y]
                                       [3 :a] :yes
                                       [4 :b] :umm-maybe
                                       :else :no))) => {:node-type :match-fn
                                                        :args-vec  '[x y]
                                                        :matchers  [[[3 :a] :yes]
                                                                    [[4 :b] :umm-maybe]]
                                                        :else-part :no}
       (match-fn-block->node '(fn [x y]
                                (match [x y]
                                       [3 :a] :yes
                                       [4 :b] :umm-maybe))) => {:node-type :match-fn
                                                                :args-vec  '[x y]
                                                                :matchers  [[[3 :a] :yes]
                                                                            [[4 :b] :umm-maybe]]
                                                                :else-part nil})

(facts "about `create-in-domain?-node`"
       (create-in-domain?-node {:node-type :match-fn
                                :args-vec  '[x y]
                                :matchers  [[[3 :a] :yes]
                                            [[4 :b] :umm-maybe]]
                                :else-part :no}) => {:node-type                    :in-domain?-fn
                                                     :args-vec                     '[x y]
                                                     :cases-with-specific-matchers [[3 :a] [4 :b]]
                                                     :has-else?                    true}
       (create-in-domain?-node {:node-type :match-fn
                                :args-vec  '[x y]
                                :matchers  [[[3 :a] :yes]
                                            [[4 :b] :umm-maybe]]
                                :else-part nil}) => {:node-type                    :in-domain?-fn
                                                     :args-vec                     '[x y]
                                                     :cases-with-specific-matchers [[3 :a] [4 :b]]
                                                     :has-else?                    false})

; (use '[clojure.core.match :only (match)]) (use 'partial-fn.core)