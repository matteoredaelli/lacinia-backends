(ns matteoredaelli.lacinia-backends
  (:require [io.pedestal.http :as http]
            [matteoredaelli.lacinia-backends-ldap.schema :as ldapschema]
            [com.walmartlabs.lacinia.pedestal2 :as lp]
            [com.walmartlabs.lacinia.schema :as schema]))

;; Use default options:
(def service (lp/default-service (ldapschema/schema) nil))

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
(defonce runnable-service (http/create-server service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (-> service
      http/create-server
      http/start))
