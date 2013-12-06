(ns example.liberator.entry
  (:require
    [liberator.core :refer (defresource by-method)]
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    [liberator.dev :refer (wrap-trace)])
  (:use
    [korma.db]
    [korma.core])
  (:import java.net.URL))

(defdb db (h2 {:db "resources/db/example;AUTO_SERVER=TRUE;DATABASE_TO_UPPER=FALSE"
                :make-pool? false}))

(defentity entries
  (pk :id)
  (table :entries))

(defn build-entry-url [request id]
  (println "R" request)
  (URL. (format "%s://%s:%s%s/%s"
          (name (:scheme request))
          (:server-name request)
          (:server-port request)
          (:uri request)
          (str id))))

(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      (slurp (io/reader body)))))

(defn parse-json [context key]
  (when (#{:put :post} (get-in context [:request :request-method]))
    (try
      (if-let [body (body-as-string context)]      
        (let [data (json/read-str body)]
          [false {key data}]) 
        {:message "No body"})
      (catch Exception e
        (.printStackTrace e)
        {:message (format "IOException: " (.getMessage e))}))))

(defn check-content-type [ctx content-types]
  (if (#{:put :post} (get-in ctx [:request :request-method]))
    (or
      (some #{(get-in ctx [:request :headers "content-type"])}
        content-types)
      [false {:message "Unsupported Content-Type"}])
    true))

(defresource list-resource
  :available-media-types ["application/json"]
  :allowed-methods [:get :post]
  :known-content-type? #(check-content-type % ["application/json"])
  :malformed? #(parse-json % ::data)
  :post! (fn [ctx]
           (insert entries (values (::data ctx))))
  :post-redirect? false
  :handle-ok (select entries))

(defresource entry-resource [id]
  :allowed-methods [:get :put :delete]
  :known-content-type? #(check-content-type % ["application/json"])
  :exists? (fn [_]
             (prn "ID" id)
             (let [e (select entries (where {:id id}))]
                    (if-not (empty? e)
                      {::entry e})))
  :existed? (fn [_] (not-empty (select entries (where {:id id}))))
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx] (::entry ctx)) 
  :delete! (fn [_] (delete :entries (where {:id id})))
  :malformed? #(parse-json % ::data)
  :can-put-to-missing? false
  :put! #(update entries
           (set-fields (::data %))
           (where {:id id}))
  :new? (fn [_] (not-empty (select entries (where {:id id})))))

