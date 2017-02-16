(ns csv2beancount.validator-test
  (:require [clojure.test :refer :all]
            [csv2beancount.validator :refer :all]
            [clojure.java.io :as io])
  (:use midje.sweet))

(def help-output (str "  -c, --csv CSV    Csv Path\n"
                      "  -y, --yaml Yaml  Yaml Path\n"
                      "  -h, --help\n"))

(def non-existing-file "/tmp/_insert_random_filename_here_")

(def existing-file (-> "single_line_transaction.csv" io/resource io/file .getAbsolutePath))

(def success-result 1)

(def success-function (fn [_ _] success-result))

(def println-help-output (str help-output "\n"))

(deftest when-invoked-with-help-should-print-it
  (let [params {:options {:help ""} :summary help-output }
        console-output (with-out-str (validate-params params success-function))]
    (is (= console-output println-help-output))))

(deftest when-invoked-without-csv-should-fail
  (let [params {:options {:csv ""} :summary help-output }
        console-output (with-out-str (validate-params params success-function))]
    (is (= console-output println-help-output))))

(deftest when-invoked-without-rules-should-fail
  (let [params {:options {:yaml ""} :summary help-output }
        console-output (with-out-str (validate-params params success-function))]
    (is (= console-output println-help-output))))

(deftest when-csv-not-exists-should-output-that 
  (let [params {:options {:csv non-existing-file :yaml non-existing-file} :summary help-output }
        console-output (with-out-str (validate-params params success-function))]
    (is (= console-output "The file provided in --csv does not exist\n"))))

(deftest when-yaml-not-exists-should-output-that 
  (let [params {:options {:csv existing-file :yaml non-existing-file} :summary help-output }
        console-output (with-out-str (validate-params params success-function))]
    (is (= console-output "The file provided in --yaml does not exist\n"))))

(deftest when-invoked-with-correct-arguments-should-execute-success-function
  (let [params {:options {:csv existing-file :yaml existing-file} :summary help-output }
        console-output (validate-params params success-function)]
    (is (= console-output success-result))))
