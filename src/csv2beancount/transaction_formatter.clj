(ns csv2beancount.transaction-formatter
  (:require [clojure.string :as str]
            [clj-time.format :as f]))

(def ^:private csv-formatter (f/formatter "dd/MM/yyyy"))

(def ^:private bc-formatter (f/formatter "yyyy-MM-dd"))

(defn- format-date [datestr]
  (f/unparse bc-formatter (f/parse csv-formatter datestr)))

(defn- header-format [transaction]
  (format "%s * \"%s\"\n" 
          (format-date (:date transaction)) 
          (:desc transaction) 
          (:comment transaction)))

(defn- header-comment-format[transaction]
  (format "%s * \"%s\" \"%s\"\n"
          (format-date (:date transaction)) 
          (:desc transaction) 
          (:comment transaction)))

(defn- format-transaction-line [account amount currency]
  (format "  %s %s %s\n" account amount currency))

(defn- format-with-comment [t]
  (str
    (header-comment-format t) 
    (format-transaction-line (:account1 t) (:amount1 t) (:currency t))
    (format-transaction-line (:account2 t) (:amount2 t) (:currency t))))

(defn- format-without-comment [t]
  (str
    (header-format t) 
    (format-transaction-line (:account1 t) (:amount1 t) (:currency t))
    (format-transaction-line (:account2 t) (:amount2 t) (:currency t))))

(defn to-beancount [transaction]
  (if (str/blank? (:comment transaction))
    (format-without-comment transaction)
    (format-with-comment transaction)))

