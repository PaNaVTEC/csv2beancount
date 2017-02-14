(ns csv2beancount.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [csv2beancount.beancountformatter :refer [convert-csv]]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:gen-class))

(def ^:private cli-options
  [["-c" "--csv CSV" "Csv Path"]
   ["-h" "--help"]])

(def ^:private required-options #{:csv})

(defn- missing-required? [options]
  (not-every? options required-options))

(defn- csv-exists? [file-path] (.exists (io/as-file file-path)))

(defn- csv-path [options] (str/trim (:csv options)))

(defn -main [& args]
  (let [{:keys [options _ summary _]} (parse-opts args cli-options)]
    (cond
      (or (:help options) (missing-required? options)) (println summary)
      (not (csv-exists? (csv-path options))) (println "The file provided in --csv argument does not exist")
      :else (convert-csv (csv-path options)))))

