(defproject zmedelis/clj-geoip "0.1-SNAPSHOT"
  :description "Thin Clojure layer on top of the Java GeoIP API.
Please have a look at the GeoIP homepage at http://www.maxmind.com/app/ip-location."
  :url "https://github.com/Norrit/clj-geoip"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :resource-paths ["resources"]
  :min-lein-version "2.0.0"
  :aot :all
  :main clj-geoip.core
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [commons-io "2.4"]
                 [com.maxmind.geoip/geoip-api "1.2.11"]]
  :profiles {:dev
             {:dependencies [[lein-marginalia "0.7.0"]]}})
