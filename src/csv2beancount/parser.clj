(ns csv2beancount.parser
  (:refer-clojure :exclude [load])
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [csv2beancount.transaction-formatter :refer [to-beancount]]
            [csv2beancount.transaction :refer [get-transaction]]
            [yaml.core :as yaml])
  (:use clojure-csv.core))

(defn- get-rules [rules-path]
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
        skip-lines (Integer. (get csv-rules "skip" 0))
        desc-index (get csv-rules "description")]
    { :delimiter delimiter :currency currency :account account
     :default-account default-account :date-index date-index
     :amount-in-index amount-in-index
     :amount-out-index amount-out-index
     :skip-lines skip-lines :desc-index desc-index
     :transactions transactions }))

(defn- get-csv [csv-path delimiter] 
  (parse-csv (io/reader csv-path) :delimiter delimiter))

(defn- get-transactions[csv-path rules-path]
  (let [rules (get-rules rules-path)
        skip-lines (:skip-lines rules)
        delimiter (:delimiter rules)]
    (for [row (drop skip-lines (get-csv csv-path delimiter))
          :let [transaction (get-transaction rules row)]
          :when (some? transaction)]
       (to-beancount transaction))))

(defn convert-csv [{:keys [csv-path yaml-path]}]
  (get-transactions csv-path yaml-path))

