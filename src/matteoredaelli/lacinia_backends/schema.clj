(ns matteoredaelli.lacinia-backends.schema
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.resolve :as resolve]
    [com.stuartsierra.component :as component]
    [matteoredaelli.lacinia-backends.ldap.schema :as ldap-schema]
    [matteoredaelli.lacinia-backends.qliksense.schema :as qliksense-schema]))

(defn load-schema
  [component]
  (let [ldap-schema (ldap-schema/get-schema component)
        qliksense-schema (qliksense-schema/get-schema component)
        schema (merge-with into ldap-schema qliksense-schema)]
    (-> schema
        (util/attach-resolvers (merge (ldap-schema/resolver-map component)
                                      (qliksense-schema/resolver-map component)
                                      ))
        schema/compile)))


(defrecord SchemaProvider [schema]

  component/Lifecycle

  (start [this]
    (assoc this :schema (load-schema this)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (-> {}
                        map->SchemaProvider
                        (component/using [:ldap-backend :qliksense-backend]))})
