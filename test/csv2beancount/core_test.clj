(ns csv2beancount.core-test
  (:require [clojure.test :refer :all]
            [csv2beancount.core :refer :all]
            [clojure.java.io :as io])
  (:use midje.sweet))

(def help-output (str "  -c, --csv CSV    Csv Path\n"
                      "  -y, --yaml Yaml  Yaml Path\n"
                      "  -h, --help\n"))

(defn abs-path-of[path] (-> path io/resource io/file .getAbsolutePath))

(deftest main-prints-usage-is-invoked-without-arguments
  (let [console-output (with-out-str (-main ""))]
    (is (= console-output help-output))))

(deftest non-existing-file-should-output-that
  (let [non-existing-file "whatever_non_existing_file.txt"
        params {:options {:csv non-existing-file :yaml non-existing-file}}
        console-output (with-out-str (run-program params))]
    (is (= console-output "The file provided in --csv does not exist\n"))))

(def single-transaction (str "2017-02-08 * \"CODURANCE\" \"Salary paid from employer\"\n"
                             "  Assets:UK:ClubLloyds 5000.00 GBP\n"
                             "  Income:UK:Codurance:Salary -5000.00 GBP\n\n"))

(deftest single-line-csv-parses-a-transaction
  (let [csv-path (abs-path-of "single_line_transaction.csv")
        yml-path (abs-path-of "simple_transaction_rules.yaml")
        params {:options {:csv csv-path :yaml yml-path} :summary help-output }
        console-output (with-out-str (run-program params))]
    (is (= console-output single-transaction))))

(def default-account-transaction (str "2017-02-08 * \"DESC TRANSACTION\"\n"
                                      "  Assets:UK:ClubLloyds 52.00 GBP\n"
                                      "  Expenses:Unknown -52.00 GBP\n\n"))

(deftest default-account-when-no-rules-match
  (let [csv-path (abs-path-of "transaction_not_in_rules.csv")
        yml-path (abs-path-of "simple_transaction_rules.yaml")
        params {:options {:csv csv-path :yaml yml-path} :summary help-output }
        console-output (with-out-str (run-program params))]
    (is (= console-output default-account-transaction))))

(deftest default-account-when-no-rules-match
  (let [csv-path (abs-path-of "transaction_with_alternative_delimiter.csv")
        yml-path (abs-path-of "alternative_delimiter_rules.yaml")
        params {:options {:csv csv-path :yaml yml-path} :summary help-output }
        console-output (with-out-str (run-program params))]
    (is (= console-output default-account-transaction))))

