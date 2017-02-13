(ns csv2beancount.core-test
  (:require [clojure.test :refer :all]
            [csv2beancount.core :refer :all])
  (:use clojure-csv.core))

(testing "First column should be a date"
  (let [rows (parse-csv "13/12/2089, Hola, Hola")
        first-row (first rows)
        first-field (first first-row)]
    (is (= first-field "13/12/2089"))))

(def help-output "  -c, --csv CSV  Csv Path\n  -h, --help\n")

(testing "Main prints usage is invoked without arguments"
  (let [console-output (with-out-str (-main ""))]
    (is (= console-output help-output))))

(testing "")
