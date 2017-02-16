(ns csv2beancount.parser
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [csv2beancount.transaction-formatter :refer [to-beancount]]
            [yaml.core :as yaml])
  (:use clojure-csv.core))

(defn- negative[x] (str "-" (str/trim x)))

(defn associated-amounts 
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

(defn get-rules [rules-path]
  (let [yaml (yaml/from-file rules-path)
        csv-rules (get yaml "csv")
        transactions (get yaml "transactions")
        delimiter (.charAt (get csv-rules "delimiter" ",") 0)
        currency (get csv-rules "currency")
        account (get csv-rules "processing_account")
        default-account (get csv-rules "default_account")
        date-index (get csv-rules "date")
        amount-in-index (get csv-rules "amount_in")
        amount-out-index (get csv-rules "amount_out")
        desc-index (get csv-rules "description")]
    { :delimiter delimiter :currency currency :account account
     :default-account default-account :date-index date-index
     :amount-in-index amount-in-index
     :amount-out-index amount-out-index
     :desc-index desc-index :transactions transactions }))

(defn- get-csv [csv-path delimiter] 
  (parse-csv (io/reader csv-path) :delimiter delimiter))

(defn- get-amounts[amount-in amount-out]
  (if (= amount-in amount-out)
    (associated-amounts amount-in)
    (associated-amounts amount-in amount-out)))

(defn- get-transaction[rules row]
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

(defn- get-transactions[csv-path rules-path]
  (let [rules (get-rules rules-path)]
    (for [row (get-csv csv-path (:delimiter rules))
          :let [transaction (get-transaction rules row)]]
       (to-beancount transaction))))

(defn convert-csv [csv-path rules-path]
  (doseq [x (get-transactions csv-path rules-path)]
    (println x)))

