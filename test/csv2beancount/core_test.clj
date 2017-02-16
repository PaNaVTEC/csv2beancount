(ns csv2beancount.core-test
  (:require [clojure.test :refer :all]
            [csv2beancount.core :refer :all]
            [clojure.java.io :as io])
  (:use clojure-csv.core)
  (:use midje.sweet))

(def help-output (str "  -c, --csv CSV    Csv Path\n"
                      "  -y, --yaml Yaml  Yaml Path\n"
                      "  -h, --help\n"))

(deftest main-prints-usage-is-invoked-without-arguments
  (let [console-output (with-out-str (-main ""))]
    (is (= console-output help-output))))

(deftest non-existing-file-should-output-that
  (let [non-existing-file "whatever_non_existing_file.txt"
        params {:options {:csv non-existing-file :yaml non-existing-file} :summary help-output }
        console-output (with-out-str (-run-program params))]
    (is (= console-output "The file provided in --csv does not exist\n"))))

(def single-transaction (str "2017-02-08 * \"CODURANCE\"\n"
                             "  Assets:UK:ClubLloyds 5000.00 GBP\n"
                             "  Income:UK:Codurance:Salary -5000.00 GBP\n\n"))

(deftest single-line-csv-parses-a-transaction
  (let [csv-path (-> "single_line_transaction.csv" io/resource io/file .getAbsolutePath)
        yml-path (-> "simple_transaction_rules.yaml" io/resource io/file .getAbsolutePath)
        params {:options {:csv csv-path :yaml yml-path} :summary help-output }
        console-output (with-out-str (-run-program params))]
    (is (= console-output single-transaction))))

(def default-account-transaction (str "2017-02-08 * \"DESC TRANSACTION\"\n"
                                      "  Assets:UK:ClubLloyds 52.00 GBP\n"
                                      "  Expenses:Unknown -52.00 GBP\n\n"))

(deftest default-account-when-no-rules-match
  (let [csv-path (-> "transaction_not_in_rules.csv" io/resource io/file .getAbsolutePath)
        yml-path (-> "simple_transaction_rules.yaml" io/resource io/file .getAbsolutePath)
        params {:options {:csv csv-path :yaml yml-path} :summary help-output }
        console-output (with-out-str (-run-program params))]
    (is (= console-output default-account-transaction))))

(deftest default-account-when-no-rules-match
  (let [csv-path (-> "transaction_with_alternative_delimiter.csv" io/resource io/file .getAbsolutePath)
        yml-path (-> "alternative_delimiter_rules.yaml" io/resource io/file .getAbsolutePath)
        params {:options {:csv csv-path :yaml yml-path} :summary help-output }
        console-output (with-out-str (-run-program params))]
    (is (= console-output default-account-transaction))))
