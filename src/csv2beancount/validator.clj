(ns csv2beancount.validator
  (:require [clojure.string :as str]
            [cats.core :as m]
            [cats.monad.either :as either]
            [clojure.java.io :as io]))

(def ^:private required-options #{:csv :yaml})

(defn- missing-required? [options]
  (not-every? options required-options))

(defn- file-not-exists? [path] (not (.exists (io/as-file path))))

(defn- csv-path [options] (str/trim (:csv options)))

(defn- rules-path [options] (str/trim (:yaml options)))

(defn- arg-not-exist [arg]
  (str "The file provided in --" (name arg) " does not exist"))

(defn validate-params [{:keys [options _ summary _]}]
  (cond
    (:help options) (either/left summary)
    (missing-required? options) (either/left summary)
    (file-not-exists? (csv-path options)) (either/left (arg-not-exist :csv))
    (file-not-exists? (rules-path options)) (either/left (arg-not-exist :yaml))
    :else (either/right {:csv-path (csv-path options)
                         :yaml-path (rules-path options)})))
