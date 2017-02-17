(ns csv2beancount.transaction-formatter
  (:require [clojure.string :as str]
            [clj-time.format :as f]))

(def ^:private bc-formatter (f/formatter "yyyy-MM-dd"))

(defn- format-date [datestr formatter]
  (f/unparse bc-formatter (f/parse formatter datestr)))

(defn- header-format [transaction formatter]
  (format "%s * \"%s\"\n" 
          (format-date (:date transaction) formatter) 
          (:desc transaction) 
          (:comment transaction)))

(defn- header-comment-format[transaction formatter]
  (format "%s * \"%s\" \"%s\"\n"
          (format-date (:date transaction) formatter) 
          (:desc transaction) 
          (:comment transaction)))

(defn- format-transaction-line [account amount currency]
  (format "  %s %s %s\n" account amount currency))

(defn- format-with-comment [t formatter]
  (str
    (header-comment-format t formatter) 
    (format-transaction-line (:account1 t) (:amount1 t) (:currency t))
    (format-transaction-line (:account2 t) (:amount2 t) (:currency t))))

(defn- format-without-comment [t formatter]
  (str
    (header-format t formatter) 
    (format-transaction-line (:account1 t) (:amount1 t) (:currency t))
    (format-transaction-line (:account2 t) (:amount2 t) (:currency t))))

(defn to-beancount [transaction date-format]
  (if (str/blank? (:comment transaction))
    (format-without-comment transaction (f/formatter date-format))
    (format-with-comment transaction (f/formatter date-format))))

