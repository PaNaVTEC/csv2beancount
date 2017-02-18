(ns csv2beancount.transaction
  (:require [clojure.string :as str]))

(defn- toggle-sign[x]
  (let [amount (str/trim x)
        is-negative (str/starts-with? amount "-")]
    (if is-negative (subs amount 1) (str "-" amount))))

(defn- amount-per-account [amount-in amount-out]
  (cond
    (and (str/blank? amount-in) (str/blank? amount-out)) {:amount1 "" :amount2 ""}
    (= amount-in amount-out) {:amount1 amount-in :amount2 (toggle-sign amount-in)}
    (str/blank? amount-in) {:amount1 (toggle-sign amount-out) :amount2 amount-out}
    :else {:amount1 amount-in :amount2 (toggle-sign amount-in)}))

(defn- get-amounts [amount-in amount-out toggle-sign?]
  (let [{:keys [amount1 amount2]} (amount-per-account amount-in amount-out)]
    (if toggle-sign?
      {:amount1 (toggle-sign amount1) :amount2 (toggle-sign amount2)}
      {:amount1 amount1 :amount2 amount2})))

(defn- associated-rule [description transaction-rules]
  (let [filtered-rules (filter #(.contains description (key %)) transaction-rules)
        rule (if (not-empty filtered-rules) (val (first filtered-rules)))]
    rule))

(defn get-transaction[rules row]
  (let [desc (get row (:desc-index rules))
        rule (associated-rule desc (:transactions rules))
        skip-transaction (get rule "skip" false)
        {:keys [amount1 amount2]} 
        (get-amounts (get row (:amount-in-index rules))
                     (get row (:amount-out-index rules)) 
                     (:toggle-sign rules))]
    (if skip-transaction nil
      {:date (get row (:date-index rules))
       :desc desc
       :currency (:currency rules)
       :account1 (:account rules)
       :account2 (get rule "account" (:default-account rules))
       :comment (get rule "comment" "")
       :amount1 amount1
       :amount2 amount2 })))
