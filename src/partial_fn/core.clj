(ns partial-fn.core
  (:import [clojure.lang IFn])
  (:use [clojure.core.match :only [match]]))

; (partial-fn [x y z]
;             [4 5 _] 4
;             :else 11)
;
; becomes
;
; (fn [x y z]
;   (match [x y z]
;          [4 5 _] 4
;          :else 11))
;
; becomes
;
; {:fn         (fn [x y z]
;                (match [x y z]
;                       [4 5 _] 4
;                       :else 11))
;  :in-domain? (fn [_ _ _] true)}

(defmacro make-keyword-map [& syms]
  `(hash-map ~@(mapcat (fn [s] [(keyword (name s)) s]) syms)))

(defn separate-matchers [ms]
  (let [last-clause (last ms)]
    (if (= (first last-clause) :else)
      [(butlast ms) (last last-clause)]
      [ms nil])))

(defn match-fn-block->node [match-fn-block]
  (let [[_ args-vec match-block] match-fn-block
        [_ _ & rest] match-block
        [matchers else-part] (separate-matchers (partition-all 2 rest))]
    (into {:node-type :match-fn}
          (make-keyword-map args-vec matchers else-part))))

(defn create-in-domain?-node [match-fn-node]
  (let [cases-with-specific-matchers (map first (:matchers match-fn-node))
        has-else? (boolean (:else-part match-fn-node))
        args-vec (:args-vec match-fn-node)]
    (into {:node-type :in-domain?-fn}
          (make-keyword-map cases-with-specific-matchers has-else? args-vec))))

(defn in-domain?-node->block [node]
  (let [syms-vec (:args-vec node)]
    (if (:has-else? node)
      `(fn ~syms-vec true)
      `(fn ~syms-vec
         (match ~syms-vec
                ~@(reduce concat (map vector (:cases-with-specific-matchers node) (repeat true)))
                :else false)))))

(defn match-fn-block->in-domain?-block [match-fn-block]
  (-> match-fn-block
      match-fn-block->node
      create-in-domain?-node
      in-domain?-node->block))

(defrecord PartialFunction [in-domain? fun]
  IFn
  (invoke [this a]
    ((:fun this) a))
  (invoke [this a b]
    ((:fun this) a b))
  (invoke [this a b c]
    ((:fun this) a b c))
  (invoke [this a b c d]
    ((:fun this) a b c d))
  (invoke [this a b c d e]
    ((:fun this) a b c d e))
  ; Do I have to repeat the above for every arity?
  (applyTo [this args]
    (apply (:fun this) args)))

(defn in-domain? [pfn & args]
  (apply (:in-domain? pfn) args))

(defmacro partial-fn [args-vec & code]
  (let [match-fn-block `(fn ~args-vec
                          (match ~args-vec ~@code))]
    `(map->PartialFunction {:fun        ~match-fn-block
                            :in-domain? ~(match-fn-block->in-domain?-block match-fn-block)})))

(defmacro define-partial-fn [var-name & rest]
  `(def ~var-name (partial-fn ~@rest)))