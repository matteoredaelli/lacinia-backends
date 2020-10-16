(ns  matteoredaelli.lacinia-backends.utils
  (:require [clojure.set]))

(defn merge-schemas
  [schema1 schema2]
  (merge-with merge schema1 schema2))
