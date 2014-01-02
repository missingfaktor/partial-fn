(ns partial-fn.core
  (:import [clojure.lang IFn])
  (:use [clojure.core.match :only [match]]))

(defmacro make-keyword-map [& syms]
  `(hash-map ~@(mapcat (fn [s] [(keyword (name s)) s]) syms)))

(defn create-in-domain?-node [match-fn-node]
  (let [cases-supported (map first (:matchers match-fn-node))
        has-else? (boolean (:else match-fn-node))]
    {:node-type       :in-domain?-fn
     :args-vec        (:args-vec match-fn-node)
     :arity           (:arity match-fn-node)
     :cases-supported cases-supported
     :has-else?       has-else?}))

(defn in-domain?-node->block [node]
  (let [syms-vec (:args-vec node)]
    (if (:has-else? node)
      `(fn ~syms-vec true)
      `(fn ~syms-vec
         (match ~syms-vec
                ~@(reduce concat (map vector (:cases-supported node) (repeat true)))
                :else false)))))

(defn separate-matchers [ms]
  (let [last-clause (last ms)]
    (if (= (first last-clause) :else)
      [(butlast ms) (last last-clause)]
      [ms nil])))

(defn match-fn-block->node [match-fn-block]
  (let [[_ args-vec match-block] match-fn-block
        arity (count args-vec)
        [_ _ & rest] match-block
        [matchers else-part] (separate-matchers (partition-all 2 rest))]
    {:node-type :match-fn
     :args-vec  args-vec
     :arity     arity
     :matchers  matchers
     :else      else-part}))

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

; (partial-fn [x y z]
;             [4 5 _] 4
;             :else 11)

; (fn [x y z]
;   (match [x y z]
;          [4 5 _] 4
;          :else 11))

; {:fn         (fn [x y z]
;                (match [x y z]
;                       [4 5 _] 4
;                       :else 11))
;  :in-domain? (fn [_ _ _] true)}

