(defproject csv2beancount "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [midje "1.8.3"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [io.forward/yaml "1.0.5"]
                 [clj-time "0.13.0"]]
  :main     csv2beancount.core
  :profiles {:dev {:plugins [[lein-midje "3.2.1"]]}})
