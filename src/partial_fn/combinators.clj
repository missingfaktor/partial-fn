(ns partial-fn.combinators
  (:use [partial-fn.core])
  (:import [partial_fn.core PartialFunction]))

(defn fn->partial-fn [fun]
  (if (instance? PartialFunction fun)
    fun
    (map->PartialFunction {:fun fun
                           :in-domain? (constantly true)})))

(defn fallback-to-nil [pfun]
  (fn [& args]
    (if (apply in-domain? pfun args)
      (apply pfun args)
      nil)))
