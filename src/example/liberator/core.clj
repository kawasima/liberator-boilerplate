(ns example.liberator.core
  (:use [liberator.core :only [resource defresource]]
    [compojure.core :only [routes defroutes ANY]]
    [org.httpkit.server :only (run-server)]
    [example.liberator entry])
  (:require
    [compojure.handler :as handler]
    [ring.middleware.reload :as reload]
    [liberator.dev :as dev]))

(defroutes app
  (->
    (routes
      (ANY ["/entry/:id" :id #".*"] [id] (entry-resource id))
      (ANY "/entries" [] list-resource))
    (dev/wrap-trace :ui :header)))

(defn in-dev? [args] true)

(defn -main [& args]
  (let [h (if (in-dev? args)
            (reload/wrap-reload (handler/site #'app))
            (handler/site app))]
    (run-server h {:port 8082})))

