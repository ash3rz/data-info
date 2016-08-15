(ns data-info.services.home
  (:require [dire.core :refer [with-pre-hook! with-post-hook!]]
            [clj-jargon.init :refer [with-jargon]]
            [clj-jargon.item-info :refer [exists?]]
            [clj-jargon.item-ops :refer [mkdirs]]
            [data-info.services.stat :as stat]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as log]
            [data-info.util.irods :as irods]
            [data-info.util.validators :as validators]
            [data-info.util.paths :as path]))

(defn- user-home-path
  [user]
  (let [user-home (path/user-home-dir user)]
    (irods/catch-jargon-io-exceptions
      (with-jargon (cfg/jargon-cfg) [cm]
        (validators/user-exists cm user)
        (when-not (exists? cm user-home)
          (mkdirs cm user-home))
        (-> (stat/path-stat cm user user-home)
            (select-keys [:id :label :path :date-created :date-modified :permission]))))))

(defn do-homedir
  [{user :user}]
  (user-home-path user))

(with-pre-hook! #'do-homedir
  (fn [params]
    (log/log-call "do-homedir" params)))

(with-post-hook! #'do-homedir (log/log-func "do-homedir"))
