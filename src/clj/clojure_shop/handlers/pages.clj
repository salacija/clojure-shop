(ns clojure-shop.handlers.pages
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.db.core :as db]))

(defn from-query [request, param-name]
  (let [param (or (get (:query-params request) param-name))]
    (if param
      (if (number? (read-string param)) (Integer. param) param)
      param)))

(defn from-query [request, param-name]
  (let [param (or (get (:query-params request) param-name))]
    (if param
      (if (number? (read-string param)) (Integer. param) param)
      param)))

(defn home [request]
  (let [categoryId (from-query request "categoryId")
        keyword (from-query request "keyword")
        db-function (if keyword db/search-products (if categoryId db/get-products-by-category-id db/get-all-products))
        db-params {:categoryId categoryId :keyword keyword}
        ]
    (layout/view request "home.html" {:categories (db/get-categories)
                                      :products (db-function db-params)})))
(defn register [_]
  (layout/view _ "register.html"))

(defn product [_]
  (if-let [product (db/get-product {:productId (from-query _ "productId")})]
    (layout/view _ "product.html" {:product product
                                   :featured-products (db/get-featured-products)})
    (layout/error-page {:error-details "Requested product doesnt' exist."}))
  )

(defn profile [_]
  (layout/view _ "profile.html"))

(defn edit-profile [_]
  (layout/view _ "edit-profile.html"))

(defn checkout [_]
  (let [products (let [cart-items (-> _ :session :cart)]
                   (map (fn [item] {:id (-> item :product :id)
                                    :name (-> item :product :name)
                                    :quantity (:quantity item)
                                    :price (* (-> item :product :price) (:quantity item))}) cart-items))]
    (layout/view _ "checkout.html" {:products products :totalPrice (reduce + (map (fn [item] (:price item)) products))})))


(defn login [_]
  (layout/view _ "login.html" {}))
