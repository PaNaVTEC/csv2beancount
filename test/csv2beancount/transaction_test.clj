(ns csv2beancount.transaction-test
  (:require [clojure.test :refer :all]
            [csv2beancount.transaction :refer :all])
  (:use midje.sweet))

(def rules {:date-index 0
            :amount-in-index 1
            :amount-out-index 2
            :desc-index 3
            :currency "GBP"
            :account "processing account"
            :transactions {"CODU" {"account" "account" "comment" "comment" }}
            })

(deftest get-transaction-with-minimal-rules
  (is (= (get-transaction rules ["30/07/1987" "123" "" "CODU"]) 
         {
          :date "30/07/1987"
          :desc "CODU"
          :currency (:currency rules)
          :account1 (:account rules)
          :account2 "account"
          :amount1 "123"
          :amount2 "-123"
          :comment "comment"
          })))

(deftest get-transaction-with-amount-in
  (is (= (get-transaction rules ["30/07/1987" "" "123" "CODU"]) 
         {
          :date "30/07/1987"
          :desc "CODU"
          :currency (:currency rules)
          :account1 (:account rules)
          :account2 "account"
          :amount1 "-123"
          :amount2 "123"
          :comment "comment"
          })))

(def rules-one-amount {:date-index 0
            :amount-in-index 1
            :amount-out-index 1
            :desc-index 2
            :currency "GBP"
            :account "processing account"
            :transactions {"CODU" {"account" "account" "comment" "comment" }}
            })

(deftest get-transaction-with-only-one-amount-positive
  (is (= (get-transaction rules-one-amount ["30/07/1987" "123" "CODU"])
         {
          :date "30/07/1987"
          :desc "CODU"
          :currency (:currency rules)
          :account1 (:account rules)
          :account2 "account"
          :amount1 "123"
          :amount2 "-123"
          :comment "comment"
          })))

(deftest get-transaction-with-only-one-amount-negative
  (is (= (get-transaction rules-one-amount ["30/07/1987" "-123" "CODU"])
         {
          :date "30/07/1987"
          :desc "CODU"
          :currency (:currency rules)
          :account1 (:account rules)
          :account2 "account"
          :amount1 "-123"
          :amount2 "123"
          :comment "comment"
          })))

(deftest get-transaction-with-no-amount
  (is (= (get-transaction rules ["30/07/1987" "" "" "CODU"])
         {
          :date "30/07/1987"
          :desc "CODU"
          :currency (:currency rules)
          :account1 (:account rules)
          :account2 "account"
          :amount1 ""
          :amount2 ""
          :comment "comment"
          })))
