(ns csv2beancount.transaction-formatter
  (:require [clojure.string :as str]
            [clj-time.format :as f]))

(def ^:private csv-formatter (f/formatter "dd/MM/yyyy"))

(def ^:private beancount-formatter (f/formatter "yyyy-MM-dd"))

(def ^:private beancount-transaction-format "%s * \"%s\"\n  %s %s %s\n  %s %s %s\n")

(defn- format-date [datestr]
  (f/unparse beancount-formatter (f/parse csv-formatter datestr)))

(defn to-beancount [transaction]
  (format beancount-transaction-format
          (format-date (:date transaction)) (:desc transaction)
          (:account1 transaction) (:amount1 transaction) (:currency transaction)
          (:account2 transaction) (:amount2 transaction) (:currency transaction)))

