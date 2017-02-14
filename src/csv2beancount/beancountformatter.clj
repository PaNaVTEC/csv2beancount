(ns csv2beancount.beancountformatter
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-time.format :as f]))

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

(defn convert-csv [options]
  (with-open [rdr (io/reader (str/trim (:csv options)))]
    (doseq [line (line-seq rdr)]
      (line-to-transaction line))))
