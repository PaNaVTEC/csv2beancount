(ns csv2beancount.parser-test
  (:require [clojure.test :refer :all]
            [csv2beancount.parser :refer :all]
            [cats.monad.either :refer [left right]]
            [clojure.java.io :as io])
  (:use midje.sweet))

(defn res-path [file-name]
  (-> file-name io/resource io/file .getAbsolutePath))

(defmacro parsing-with-args-should [csv-param yaml-param expectation]
  `(let [csv-path# (res-path ~csv-param)
         yaml-path# (res-path ~yaml-param)
         transactions# (convert-csv {:csv-path csv-path# :yaml-path yaml-path#})]
     (is (= (right ~expectation) transactions#))))

(deftest parser-with-no-transactions-should-print-empty
  (parsing-with-args-should "empty_transactions.csv"
                            "simple_transaction_rules.yaml"
                            []))

(deftest parser-with-header-should-skip-it
  (parsing-with-args-should  "transaction_with_header.csv"
                             "simple_with_skip.yaml"
                             '("2017-10-10 * \"DESC\"\n  Assets:UK:ClubLloyds 123 GBP\n  Expenses:Unknown -123 GBP\n")))

(deftest rule-with-skip-should-skip-transaction
  (parsing-with-args-should   "single_line_transaction.csv"
                              "skip_codurance_desc.yaml"
                              []))

(deftest yaml-with-custom-date-format-should-interpret-it
  (parsing-with-args-should   "single_line_transaction_custom_date_format.csv"
                              "custom_date_format.yaml"
                              '("2017-10-10 * \"DESC\"\n  Assets:UK:ClubLloyds 123 GBP\n  Expenses:Unknown -123 GBP\n")))

(deftest yaml-with-toggle-sign-should-reverse-it
  (parsing-with-args-should   "toggle_sign.csv"
                              "toggle_sign.yaml"
                              '("2017-10-10 * \"DESC\"\n  Assets:UK:ClubLloyds 123 GBP\n  Expenses:Unknown -123 GBP\n")))
