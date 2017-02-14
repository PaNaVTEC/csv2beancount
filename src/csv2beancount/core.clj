(ns csv2beancount.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [csv2beancount.beancountformatter :refer [convert-csv]])
  (:gen-class))

(def ^:private cli-options
  [["-c" "--csv CSV" "Csv Path"]
   ["-h" "--help"]])

(def ^:private required-options #{:csv})

(defn- missing-required? [options]
  (not-every? options required-options))

(defn -main [& args]
  (let [{:keys [options arguments summary errors]}
        (parse-opts args cli-options)]
    (if (or (:help options)
            (missing-required? options))
      (println summary)
      (convert-csv options))))

