(ns data-info.routes.navigation
  (:use [compojure.api.sweet]
        [data-info.routes.domain.common]
        [data-info.routes.domain.navigation]
        [data-info.routes.domain.stats])
  (:require [data-info.services.directory :as dir]
            [data-info.services.root :as root]
            [data-info.util.service :as svc]))

(defroutes* navigation

  (context* "/navigation" []
    :tags ["Navigation"]

    (GET* "/root" [:as {uri :uri}]
      :query [{:keys [user]} SecuredQueryParamsRequired]
      :return NavigationRootResponse
      :summary "Root Listing"
      :description (str
"This endpoint provides a shortcut for the client to list the top-level directories (e.g. the user's
 home directory, trash, and shared directories)."
(get-error-code-block
  "ERR_DOES_NOT_EXIST, ERR_NOT_READABLE, ERR_NOT_A_USER"))
      (svc/trap uri root/do-root-listing user))

    (GET* "/path/:zone/*" [:as {{path :*} :params uri :uri}]
      :path-params [zone :- String]
      :query [params SecuredQueryParamsRequired]
      :return NavigationResponse
      :no-doc true
      (svc/trap uri dir/do-directory zone path params))

    ;; This is actually handled by the above route, which cannot be documented properly.
    (GET* "/path/:zone/:path" [:as {uri :uri}]
      :path-params [zone :- (describe String "The IRODS zone")
                    path :- (describe String "The IRODS path under the zone")]
      :query [params SecuredQueryParamsRequired]
      :return NavigationResponse
      :summary "Directory List (Non-Recursive)"
      :description (str
                     "Only lists subdirectories of the directory path passed into it."
                     (get-error-code-block
                       "ERR_DOES_NOT_EXIST, ERR_NOT_READABLE, ERR_NOT_A_USER, ERR_NOT_A_FOLDER"))
      {:status 501})))
