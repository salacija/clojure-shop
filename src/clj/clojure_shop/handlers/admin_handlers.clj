(ns clojure_shop.handlers.admin_handlers
  (:require
       [clojure-shop.db.core :as db]
       [clojure-shop.helpers.uploads :as upload]
       [ring.util.response :refer [redirect]]))

(defn handle-product [_ query-fn]
  (if-let [success (query-fn (conj (:params _) {:image (:filename (:file (:params _)))}))]
    (do (upload/upload-handler (:file (:params _ ))) (redirect "/admin/products"))
    (redirect "error")))

(defn add-product [_]
  (handle-product _ db/create-product!))

(defn update-product [_]
  (handle-product _ db/update-product!))

(defn delete [_ delete-fn redirect-to]
  (if-let [id (:id (:path-params _))]
    (try
      (do (delete-fn {:id (Integer. id)}) (redirect (str "/admin/" redirect-to))))
    (redirect "/error")))





