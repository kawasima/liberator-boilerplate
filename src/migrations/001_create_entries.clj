(ns migrations.001-create-entries
  (:use [korma core db]))

(defn up []
  (transaction
    (exec-raw (str "CREATE TABLE entries ("
                "id BIGINT identity,"
                "name VARCHAR(255) NOT NULL"
                ")"))))

(defn down []
  (transaction
    (exec-raw "DROP TABLE entries")))

