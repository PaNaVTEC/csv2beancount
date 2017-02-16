(ns csv2beancount.validator
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def ^:private required-options #{:csv :yaml})

(defn- missing-required? [options]
  (not-every? options required-options))

(defn- file-not-exists? [path] (not (.exists (io/as-file path))))

(defn- csv-path [options] (str/trim (:csv options)))

(defn- rules-path [options] (str/trim (:yaml options)))

(defn- arg-not-exist[arg] 
  (str "The file provided in --" (name arg) " does not exist"))

(defn validate-params [{:keys [options _ summary _]} success]
  (cond
    (:help options) (println summary)
    (missing-required? options) (println summary)
    (file-not-exists? (csv-path options)) (println (arg-not-exist :csv))
    (file-not-exists? (rules-path options)) (println (arg-not-exist :yaml))
    :else (success (csv-path options) (rules-path options))))
