(ns partial-fn.core-test
  (:use [midje.sweet]
        [partial-fn.core]))

(def sample-pfn (partial-fn [x y]
                            [3 :a] :hello
                            [4 :b] :world))

(def another-sample-pfn (partial-fn [x y]
                                    :else :always))

(facts "about partial functions"
       (sample-pfn 3 :a) => :hello
       (sample-pfn 4 :b) => :world
       (sample-pfn :whoopty :do) => (throws Exception)
       (another-sample-pfn :whoopty :do) => :always
       (in-domain? sample-pfn 3 :a) => true
       (in-domain? sample-pfn 4 :b) => true
       (in-domain? sample-pfn :whoopty :do) => false
       (in-domain? another-sample-pfn :whoopty :do) => true)

(facts "about `define-partial-fn`"
       (define-partial-fn foo [a]
                          [:a] :ok
                          :else :ko) =expands-to=> (def foo
                                                     (partial-fn.core/partial-fn [a]
                                                                                 [:a] :ok
                                                                                 :else :ko)))