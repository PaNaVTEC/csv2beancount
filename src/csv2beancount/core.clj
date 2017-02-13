(ns csv2beancount.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [["-c" "--csv CSV" "Csv Path"]
   ["-h" "--help"]])

(def required-options #{:csv})

(defn missing-required? [options]
  (not-every? options required-options))

(defn convert-csv [options]
  (println "Hola que ase"))

(defn -main [& args]
  (let [{:keys [options arguments summary errors]}
        (parse-opts args cli-options)]
    (if (or (:help options)
            (missing-required? options))
      (println summary)
      (convert-csv options))))

