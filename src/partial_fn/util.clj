(ns partial-fn.util)

(defmacro make-keyword-map [& syms]
  `(hash-map ~@(mapcat (fn [s] [(keyword (name s)) s]) syms)))
