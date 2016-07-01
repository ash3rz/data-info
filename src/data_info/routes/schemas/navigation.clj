(ns data-info.routes.schemas.navigation
  (:use [common-swagger-api.schema :only [describe]]
        [data-info.routes.schemas.stats])
  (:require [schema.core :as s]))

(s/defschema RootListing
  (dissoc DataStatInfo :type))

(s/defschema NavigationRootResponse
  {:roots [RootListing]})

(s/defschema FolderListing
  (-> DataStatInfo
      (dissoc :type)
      (assoc (s/optional-key :folders)
             (describe [(s/recursive #'FolderListing)] "Subdirectories of this directory"))))

(s/defschema NavigationResponse
  {:folder FolderListing})
