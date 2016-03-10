(ns data-info.routes
  (:use [clojure-commons.lcase-params :only [wrap-lcase-params]]
        [clojure-commons.query-params :only [wrap-query-params]]
        [common-swagger-api.schema]
        [compojure.api.middleware :only [wrap-exceptions]]
        [service-logging.middleware :only [log-validation-errors add-user-to-context]]
        [ring.util.response :only [redirect]])
  (:require [compojure.route :as route]
            [clojure-commons.exception :as cx]
            [data-info.routes.avus :as avus-routes]
            [data-info.routes.data :as data-routes]
            [data-info.routes.exists :as exists-routes]
            [data-info.routes.filetypes :as filetypes-routes]
            [data-info.routes.permissions :as permission-routes]
            [data-info.routes.navigation :as navigation-routes]
            [data-info.routes.rename :as rename-routes]
            [data-info.routes.sharing :as sharing-routes]
            [data-info.routes.status :as status-routes]
            [data-info.routes.stats :as stat-routes]
            [data-info.routes.trash :as trash-routes]
            [data-info.util :as util]
            [data-info.util.config :as config]
            [data-info.util.service :as svc]
            [ring.middleware.keyword-params :as params]))

(defapi app
  (swagger-ui config/docs-uri
    :supported-submit-methods ["get", "post", "put", "delete", "patch", "head"]
    :validator-url nil)
  (swagger-docs
    {:info {:title "Discovery Environment Data Info API"
            :description "Documentation for the Discovery Environment Data Info REST API"
            :version "2.0.0"}
     :tags [{:name "service-info", :description "Service Information"}
            {:name "data-by-id", :description "Data Operations (by ID)"}
            {:name "data", :description "Data Operations"}
            {:name "bulk", :description "Bulk Operations"}
            {:name "navigation", :description "Navigation"}
            {:name "filetypes", :description "File Type Metadata"}]})
  (middlewares
    [add-user-to-context
     wrap-query-params
     wrap-lcase-params
     params/wrap-keyword-params
     (wrap-exceptions cx/exception-handlers)
     util/req-logger
     log-validation-errors]
    status-routes/status
    data-routes/data-operations
    rename-routes/rename-routes
    avus-routes/avus-routes
    exists-routes/existence-marker
    filetypes-routes/filetypes-operations
    permission-routes/permissions-routes
    navigation-routes/navigation
    stat-routes/stat-gatherer
    sharing-routes/sharing-routes
    trash-routes/trash
    (route/not-found (svc/unrecognized-path-response))))
