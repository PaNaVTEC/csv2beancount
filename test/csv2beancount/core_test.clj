(ns csv2beancount.core-test
  (:require [clojure.test :refer :all]
            [csv2beancount.core :refer :all]
            [clojure.java.io :as io])
  (:use clojure-csv.core))

(deftest first-column-should-be-a-date
  (let [rows (parse-csv "13/12/2089, Hola, Hola")
        first-row (first rows)
        first-field (first first-row)]
    (is (= first-field "13/12/2089"))))

(def help-output "  -c, --csv CSV  Csv Path\n  -h, --help\n")

(deftest main-prints-usage-is-invoked-without-arguments
  (let [console-output (with-out-str (-main ""))]
    (is (= console-output help-output))))

(def single-transaction "2017-02-08 * \"RYMAN 1164\"\n  Account 38.48 GBP\n")

(deftest single-line-csv-parses-a-transaction
  (let [csv-file (-> "single_line_transaction.csv" io/resource io/file)
        csv-path (.getAbsolutePath csv-file)
        params (str "-c " csv-path)
        console-output (with-out-str (-main params))]
  (is (= console-output single-transaction))))

(deftest non-existing-file-should-output-that
  (let [non-existing-file "whatever_non_existing_file.txt"
       params (str "-c " non-existing-file)
       console-output (with-out-str (-main params))]
  (is (= console-output "The file provided in --csv argument does not exist\n"))))
