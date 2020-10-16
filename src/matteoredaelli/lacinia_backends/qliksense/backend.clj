(ns matteoredaelli.lacinia-backends.qliksense.backend
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.log :as log]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [matteoredaelli.qliksense.api :as qliksense-api]
   [matteoredaelli.qliksense.core :as qliksense-core]
   [clojure.string :as str])
  )

(defn ^:private pooled-data-source
  []
  (->
   (System/getenv "LACINIA_BACKEND_QLIKSENSE_CONFIG")
   slurp
   edn/read-string))


(defrecord QliksenseBackend [ds]

  component/Lifecycle

  (start [this]
    (assoc this
           :ds (pooled-data-source)))

  (stop [this]
    (assoc this :ds nil)))

(defn new-backend
  []
  {:qliksense-backend (map->QliksenseBackend {})})


(defn get-objects-by-filter
  [component system path filter-raw]
  (log/debug :component component
             :filter  filter
             :system system
             :filter filter-raw)
  (let [system_map (qliksense-core/get-system-map (:ds component) system)
        filter (if (= filter-raw "") "1 eq 1" filter-raw)
        params {"filter" filter}]
    (def resp (qliksense-api/get-request system_map path params))
    (resp :body)))
