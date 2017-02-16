(ns csv2beancount.beancountformatter
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-time.format :as f]
            [yaml.core :as yaml])
  (:use clojure-csv.core))

(def ^:private csv-formatter (f/formatter "dd/MM/yyyy"))

(def ^:private beancount-formatter (f/formatter "yyyy-MM-dd"))

(def ^:private beancount-transaction-format "%s * \"%s\"\n  %s %s %s\n  %s %s %s\n")

(defn- format-date [datestr]
  (f/unparse beancount-formatter (f/parse csv-formatter datestr)))

(defn- to-beancount [transaction]
  (format beancount-transaction-format
          (format-date (:date transaction)) (:desc transaction)
          (:account1 transaction) (:amount1 transaction) (:currency transaction)
          (:account2 transaction) (:amount2 transaction) (:currency transaction)))

(defn- write-transaction [transaction]
  (println (to-beancount transaction)))

(defn associate-amounts ([transaction amount_in amount_out]
   (cond
     (str/blank? amount_in) (conj transaction {:amount1 (str "-" (str/trim amount_out)) :amount2 amount_out })
     (str/blank? amount_out) (conj transaction {:amount1 amount_in :amount2 (str "-" (str/trim amount_in)) })))
  ([transaction amount] 
   (let [trimmed-amount (str/trim amount)
         is-negative (str/starts-with? trimmed-amount "-")
         amount2 (if is-negative (subs amount 1) (str "-" trimmed-amount))]
    (conj transaction {:amount1 amount :amount2 amount2}))))

(defn- associate-account [transaction rules default-account]
  (let [filtered-rules (filter #(.contains (:desc transaction) (key %)) rules)
        rule (if (not-empty filtered-rules) (val (first filtered-rules)))]
    (conj transaction {:account2 (get rule "account" default-account)})))

(defn- line-to-transaction[line rules]
  (let [csv-rules (get rules "csv")
        delimiter (.charAt (get csv-rules "delimiter" ",") 0)
        fields (first (parse-csv line :delimiter delimiter))
        currency (get csv-rules "currency")
        account (get csv-rules "processing_account")
        default-account (get csv-rules "default_account")
        date (get fields (get csv-rules "date"))
        index-amount-in (get csv-rules "amount_in")
        index-amount-out (get csv-rules "amount_out")
        amount_in (get fields index-amount-in)
        amount_out (get fields index-amount-out)
        desc (get fields (get csv-rules "description"))
        transaction {:date date :desc desc :currency currency :account1 account}
        transaction-with-amount (if (= index-amount-in index-amount-out) (associate-amounts transaction amount_in) (associate-amounts transaction amount_in amount_out))
        complete-transaction (associate-account transaction-with-amount (get rules "transactions") default-account)]
    (write-transaction complete-transaction)))

(defn convert-csv [file-path rules-path]
  (let [rules (yaml/from-file rules-path)]
    (with-open [rdr (io/reader file-path)]
      (doseq [line (line-seq rdr)]
        (line-to-transaction line rules)))))

