(ns csv2beancount.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-time.format :as f])
  (:gen-class))

(def ^:private cli-options
  [["-c" "--csv CSV" "Csv Path"]
   ["-h" "--help"]])

(def ^:private required-options #{:csv})

(defn- missing-required? [options]
  (not-every? options required-options))

(def ^:private csv-formatter (f/formatter "dd/MM/yyyy"))

(def ^:private beancount-formatter (f/formatter "yyyy-MM-dd"))

(defn- format-date[datestr]
  (f/unparse beancount-formatter (f/parse csv-formatter datestr)))

(defn- to-beancount[transaction]
  (str (format-date (:date transaction)) " * \"" (:desc transaction) "\"\n"
       "  Account " (:amount transaction) " GBP"))

(defn- write-transaction[transaction]
  (println (to-beancount transaction)))

(defn- line-to-transaction[line]
  (let [[date _ _ desc amount & fields] (str/split line #",")]
    (write-transaction {:date date :desc desc :amount amount})))

(defn- convert-csv [options]
  (with-open [rdr (io/reader (str/trim (:csv options)))]
    (doseq [line (line-seq rdr)]
      (line-to-transaction line))))

(defn -main [& args]
  (let [{:keys [options arguments summary errors]}
        (parse-opts args cli-options)]
    (if (or (:help options)
            (missing-required? options))
      (println summary)
      (convert-csv options))))

