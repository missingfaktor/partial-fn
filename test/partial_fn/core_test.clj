(ns partial-fn.core-test
  (:use [midje.sweet]
        [partial-fn.core]))

(def sample-pfun (partial-fn [x y]
                            [3 :a] :hello
                            [4 :b] :world))

(def another-sample-pfun (partial-fn [x y]
                                    :else :always))

(facts "about partial functions"
       (sample-pfun 3 :a) => :hello
       (sample-pfun 4 :b) => :world
       (sample-pfun :whoopty :do) => (throws Exception)
       (another-sample-pfun :whoopty :do) => :always
       (in-domain? sample-pfun 3 :a) => true
       (in-domain? sample-pfun 4 :b) => true
       (in-domain? sample-pfun :whoopty :do) => false
       (in-domain? another-sample-pfun :whoopty :do) => true)

(facts "about `define-partial-fn`"
       (define-partial-fn foo [a]
                          [:a] :ok
                          :else :ko) =expands-to=> (def foo
                                                     (partial-fn.core/partial-fn [a]
                                                                                 [:a] :ok
                                                                                 :else :ko)))