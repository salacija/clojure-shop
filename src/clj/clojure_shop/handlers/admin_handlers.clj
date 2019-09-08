(ns clojure_shop.handlers.admin_handlers
  (:require
       [clojure-shop.db.core :as db]
       [clojure-shop.helpers.uploads :as upload]
       [ring.util.response :refer [redirect]]
       [ring.util.http-response :refer [ok]]))


(defn handle-product [_ query-fn]
  (let [product (:params _)
      shouldUploadImage  (> (:size (:file product)) 0)
      image (:file product)
      fileName (if
                 (clojure.string/blank? (:filename product))
                 (if
                   (clojure.string/blank? (:image product)) "default.jpg" (:image product))
                 (:filename (product)))  ]
    (do
      (if shouldUploadImage
        (upload/upload-handler image))
      (query-fn (conj product {:image fileName
                               :isNew (not (empty? (:isNew product)))
                               :isBestSeller (not (empty? (:isBestSeller product)))}))
      (redirect "/admin/products"))))

(defn add-product [_]
  (handle-product _ db/create-product!))

(defn update-product [_]
  (handle-product _ db/update-product!))

(defn delete [_ delete-fn redirect-to]
  (if-let [id (:id (:path-params _))]
    (try
      (do (delete-fn {:id (Integer. id)}) (redirect (str "/admin/" redirect-to))))
    (redirect "/error")))

(defn handle-category [_ query-fn]
  (if-let [success (query-fn (:params _))]
    (redirect "/admin/categories")
    (redirect "error")))

(defn add-category [_] (handle-category _ db/create-category!))
(defn update-category [_] (handle-category _ db/update-category!))




