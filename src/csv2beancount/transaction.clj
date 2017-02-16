(ns csv2beancount.transaction
  (:require [clojure.string :as str]))

(defn- negative[x] (str "-" (str/trim x)))

(defn- toggle-sign[x] 
  (let [amount (str/trim x)
        is-negative (str/starts-with? amount "-")]
    (if is-negative (subs amount 1) (str "-" amount))))

(defn- associated-amounts [amount-in amount-out]
   (cond
     (and (str/blank? amount-in) (str/blank? amount-out)) {:amount1 "" :amount2 ""}
     (= amount-in amount-out) {:amount1 amount-in :amount2 (toggle-sign amount-in)}
     (str/blank? amount-in) {:amount1 (negative amount-out) :amount2 amount-out}
     (str/blank? amount-out) {:amount1 amount-in :amount2 (negative amount-in)}))

(defn- associated-account [description rules default-account]
  (let [filtered-rules (filter #(.contains description (key %)) rules)
        rule (if (not-empty filtered-rules) (val (first filtered-rules)))]
    (get rule "account" default-account)))

(defn get-transaction[rules row]
  (let [desc (get row (:desc-index rules))
        {:keys [amount1 amount2]} 
        (associated-amounts (get row (:amount-in-index rules))
                            (get row (:amount-out-index rules)))]
    {:date (get row (:date-index rules))
     :desc desc
     :currency (:currency rules)
     :account1 (:account rules)
     :account2 (associated-account desc (:transactions rules) (:default-account rules))
     :amount1 amount1
     :amount2 amount2 }))
