(ns matteoredaelli.lacinia-backends.system
  (:require
   [com.stuartsierra.component :as component]
   [matteoredaelli.lacinia-backends.server :as server]
   [matteoredaelli.lacinia-backends.schema :as schema]
   [matteoredaelli.lacinia-backends.ldap.backend      :as ldap-backend]
   [matteoredaelli.lacinia-backends.qliksense.backend :as qliksense-backend]
))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)
         (merge-with into
                     (ldap-backend/new-backend)
                     (qliksense-backend/new-backend)
                )))
