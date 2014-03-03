(ns clj-geoip.core
  "Thin Clojure layer on top of the GeoIP Java API.
  Use `geoip-init` and `geoip-close` to start and stop the service and `lookup` to
  lookup information about the given IP."
  (:require [clojure.java.io :refer :all])
  (:import [com.maxmind.geoip LookupService]
           [org.apache.commons.io FileUtils])
  (:gen-class :main true))

(defn- lookup-location
  "Looks up IP location information."
  [{:keys [city]}]
  (fn [ip] 
    (if-let [location (.getLocation city ip)]
      {:ip ip
       :countryCode (.countryCode location)
       :countryName (.countryName location)
       :region (.region location)
       :city (.city location)
       :postalCode (.postalCode location)
       :latitude (.latitude location)
       :longitude (.longitude location)
       :dma-code (.dma_code location)
       :area-code (.area_code location)
       :metro-code (.metro_code location)})))

(defn- lookup-asn
  "Looks up IP provider information."
  [{:keys [asn]}]
  (fn [ip]
    (if-let [asn (.getOrg asn ip)]
      {:ip ip
       :asn asn})))

(defn lookup
  "Looks up all available IP information."
  [dbs]
  (let [loc (lookup-location dbs)
        asn (lookup-asn dbs)] 
    (fn [ip] 
      (if-let [geoinfo (merge (loc ip)
                              (asn ip))]
        geoinfo
        {:error "IP not localized"}))))

(defn- geoip-mode
  "Looks up the matching mode to the given keyword."
  [mode]
  (case mode
    :memory 1
    :check  2
    :index  4
    0))

(defn- geoip-init-db
  "Initializes a new LookupService with the given file and mode."
  [db mode]
  (if mode
    (LookupService. db (geoip-mode mode))
    (LookupService. db))) 

(defn get-db
  [db-name mode]
  (let [url (resource db-name)
        f (as-file (str "/tmp/" db-name))]
    (FileUtils/copyURLToFile url f)
    (geoip-init-db f mode)))

(defn geoip-init
  "Initializes the GeoIP service.
  The modes `:memory`, `:check` or `:index` are possible.
  Returns the Lookup function"
  [& {:keys [city-db asn-db mode] :or {mode :memory}}]
  (with-open [city (get-db (or city-db "GeoLiteCity.dat") mode)
              asn (get-db (or asn-db "GeoIPASNum.dat") mode)]
    (lookup {:city city :asn asn})))
