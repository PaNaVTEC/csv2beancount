(ns csv2beancount.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [csv2beancount.parser :refer [convert-csv]]
            [cats.monad.either :as either]
            [cats.core :as c]
            [csv2beancount.validator :refer [validate-params]])
  (:gen-class))

(def ^:private cli-options
  [["-c" "--csv CSV" "Csv Path"]
   ["-y" "--yaml Yaml" "Yaml Path"]
   ["-h" "--help"]])

(defn- print-transactions [transactions]
  (doseq [x transactions] (println x)))

(defn- print-error [error] (println error))

(defn- printresult [mresult]
  (if (either/left? mresult)
    (print-error @mresult)
    (print-transactions @mresult)))

(defn run-program [params]
  (-> (c/alet [validated-params (validate-params params)
               transactions (convert-csv validated-params)]
              transactions)
      printresult))

(defn -main [& args]
  (run-program (parse-opts args cli-options)))
