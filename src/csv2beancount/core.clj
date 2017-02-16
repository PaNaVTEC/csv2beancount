(ns csv2beancount.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [csv2beancount.beancountformatter :refer [convert-csv]]
            [csv2beancount.validator :refer [validate-params]])
  (:gen-class))

(def ^:private cli-options
  [["-c" "--csv CSV" "Csv Path"]
   ["-y" "--yaml Yaml" "Yaml Path"]
   ["-h" "--help"]])

(defn -run-program[params]
  (validate-params params (partial convert-csv)))

(defn -main [& args]
  (-run-program (parse-opts args cli-options)))
