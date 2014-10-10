(ns data-info.services.home
  (:require [dire.core :refer [with-pre-hook! with-post-hook!]]
            [clj-jargon.init :refer [with-jargon]]
            [clj-jargon.item-info :refer [exists?]]
            [clj-jargon.item-ops :refer [mkdirs]]
            [clojure-commons.validators :as cv]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as log]
            [data-info.util.validators :as validators]
            [data-info.services.common-paths :as path]
            [data-info.services.uuids :as uuid]))


(defn- user-home-path
  [user]
  (let [user-home (path/user-home-dir user)]
    (with-jargon (cfg/jargon-cfg) [cm]
      (validators/user-exists cm user)
      (when-not (exists? cm user-home)
        (mkdirs cm user-home))
      {:id   (uuid/lookup-uuid cm user-home)
       :path user-home})))


(defn do-homedir
  [{user :user}]
  (user-home-path user))

(with-pre-hook! #'do-homedir
  (fn [params]
    (log/log-call "do-homedir" params)
    (cv/validate-map params {:user string?})))

(with-post-hook! #'do-homedir (log/log-func "do-homedir"))
