(ns matteoredaelli.lacinia-backends.qliksense.schema
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :as util]
    [io.pedestal.log :as log]
    [com.stuartsierra.component :as component]
    [matteoredaelli.lacinia-backends.qliksense.backend :as backend]))


(defn qliksense-get-objects
  [backend context arguments value path]
  (let [{:keys [system filter]} arguments]
    (backend/get-objects-by-filter backend (keyword system) path filter)))

(defn query-qliksense-apps
  [backend]
  (fn [context arguments value]
    (qliksense-get-objects backend context arguments value "/qrs/app/full")))

(defn query-qliksense-custom-property-definitions
  [backend]
  (fn [context arguments value]
    (qliksense-get-objects backend context arguments value "/qrs/custompropertydefinition/full")))

(defn query-qliksense-reload-tasks
  [backend]
  (fn [context arguments value]
    (qliksense-get-objects backend context arguments value "/qrs/reloadtask/full")))

(defn query-qliksense-streams
  [backend]
  (fn [context arguments value]
    (log/debug :context context
               :arguments  arguments
               :value value)
    (qliksense-get-objects backend context arguments value "/qrs/stream/full")))

(defn query-qliksense-users
  [backend]
  (fn [context arguments value]
    (qliksense-get-objects backend context arguments value "/qrs/user/full")))


(defn resolver-map
  [component]
  (let [backend (:qliksense-backend component)]
    {
     :query/qliksense-apps (query-qliksense-apps backend)
     :query/qliksense-custom-property-definitions (query-qliksense-custom-property-definitions backend)
     :query/qliksense-reload-tasks (query-qliksense-reload-tasks backend)
     :query/qliksense-streams (query-qliksense-streams backend)
     :query/qliksense-users (query-qliksense-users backend)
     }
    ))

(defn get-schema
  [component]
  (-> (io/resource "qliksense/schema.edn")
      slurp
      edn/read-string))


(defrecord SchemaProvider [schema]

  component/Lifecycle

  (start [this]
    (assoc this :qliksense-schema (get-schema this)))

  (stop [this]
    (assoc this :qliksense-schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (-> {}
                        map->SchemaProvider
                        (component/using [:qliksense-backend]))})
