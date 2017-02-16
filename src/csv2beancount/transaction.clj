(ns csv2beancount.transaction
  (:require [clojure.string :as str]))

(defn- negative[x] (str "-" (str/trim x)))

(defn- associated-amounts 
  ([amount_in amount_out]
   (cond
     (str/blank? amount_in) {:amount1 (negative amount_out) :amount2 amount_out}
     (str/blank? amount_out) {:amount1 amount_in :amount2 (negative amount_in)}))
  ([amount] 
   (let [trimmed-amount (str/trim amount)
         is-negative (str/starts-with? trimmed-amount "-")
         amount2 (if is-negative (subs amount 1) (str "-" trimmed-amount))]
     {:amount1 amount :amount2 amount2})))

(defn- associated-account [description rules default-account]
  (let [filtered-rules (filter #(.contains description (key %)) rules)
        rule (if (not-empty filtered-rules) (val (first filtered-rules)))]
    (get rule "account" default-account)))

(defn- get-amounts[amount-in amount-out]
  (if (= amount-in amount-out)
    (associated-amounts amount-in)
    (associated-amounts amount-in amount-out)))

(defn get-transaction[rules row]
  (let [desc (get row (:desc-index rules))
        {:keys [amount1 amount2]} 
        (get-amounts (get row (:amount-in-index rules))
                     (get row (:amount-out-index rules)))]
    {:date (get row (:date-index rules))
     :desc desc
     :currency (:currency rules)
     :account1 (:account rules)
     :account2 (associated-account desc (:transactions rules) (:default-account rules))
     :amount1 amount1
     :amount2 amount2 }))
