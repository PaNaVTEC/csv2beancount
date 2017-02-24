(ns csv2beancount.validator-test
  (:require [clojure.test :refer :all]
            [csv2beancount.validator :refer :all]
            [cats.monad.either :refer [left right]]
            [clojure.java.io :as io])
  (:use midje.sweet))

(def help-output (str "  -c, --csv CSV    Csv Path\n"
                      "  -y, --yaml Yaml  Yaml Path\n"
                      "  -h, --help\n"))

(def non-existing-file "/tmp/_insert_random_filename_here_")

(def existing-file (-> "single_line_transaction.csv"
                       io/resource io/file
                       .getAbsolutePath))

(def success-result 1)

(def println-help-output help-output)

(defmacro validate-param-should [options expectation]
  `(let [params# {:options ~options :summary ~help-output}
         validated-params# (validate-params params#)]
     (is (= validated-params# ~expectation))))

(deftest when-invoked-with-help-should-print-it
  (validate-param-should {:help ""} (left println-help-output)))

(deftest when-invoked-without-csv-should-fail
  (validate-param-should {:csv ""} (left println-help-output)))

(deftest when-invoked-without-rules-should-fail
  (validate-param-should {:yaml ""} (left println-help-output)))

(deftest when-csv-not-exists-should-output-that
  (validate-param-should {:csv non-existing-file :yaml non-existing-file}
                         (left "The file provided in --csv does not exist")))

(deftest when-yaml-not-exists-should-output-that
  (validate-param-should {:csv existing-file :yaml non-existing-file}
                         (left "The file provided in --yaml does not exist")))

(deftest when-invoked-with-correct-arguments-should-parse-arguments
  (validate-param-should {:csv existing-file :yaml existing-file}
                         (right {:csv-path existing-file :yaml-path existing-file})))
