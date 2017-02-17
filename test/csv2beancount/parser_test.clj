(ns csv2beancount.parser-test
  (:require [clojure.test :refer :all]
            [csv2beancount.parser :refer :all]
            [clojure.java.io :as io])
  (:use midje.sweet))

(defn res-path[file-name]
  (-> file-name io/resource io/file .getAbsolutePath))

(deftest parser-with-no-transactions-should-print-empty
  (let [csv-path (res-path "empty_transactions.csv")
        yml-path (res-path "simple_transaction_rules.yaml")
        transactions (convert-csv {:csv-path csv-path :yaml-path yml-path})]
    (is (= [] transactions))))

(deftest parser-with-header-should-skip-it
  (let [csv-path (res-path "transaction_with_header.csv")
        yml-path (res-path "simple_with_skip.yaml")
        transactions (convert-csv {:csv-path csv-path :yaml-path yml-path})]
    (is (= '("2017-10-10 * \"DESC\"\n  Assets:UK:ClubLloyds 123 GBP\n  Expenses:Unknown -123 GBP\n")
           transactions))))

(deftest rule-with-skip-should-skip-transaction
  (let [csv-path (res-path "single_line_transaction.csv")
        yml-path (res-path "skip_codurance_desc.yaml")
        transactions (convert-csv {:csv-path csv-path :yaml-path yml-path})]
    (is (= [] transactions))))

(deftest yaml-with-custom-date-format-should-interpret-it 
  (let [csv-path (res-path "single_line_transaction_custom_date_format.csv")
        yml-path (res-path "custom_date_format.yaml")
        transactions (convert-csv {:csv-path csv-path :yaml-path yml-path})]
    (is (= '("2017-10-10 * \"DESC\"\n  Assets:UK:ClubLloyds 123 GBP\n  Expenses:Unknown -123 GBP\n")
           transactions))))
