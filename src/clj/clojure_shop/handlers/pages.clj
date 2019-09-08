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
  (layout/view request "home.html" {:categories (db/get-categories)
                                    :products (db/get-products-by-category-id {:categoryId (from-query request "categoryId")})}))
(defn register [_]
  (layout/view _ "register.html"))

(defn product [_]
  (if-let [product (db/get-product {:productId (from-query _ "productId")})]
    (layout/view _ "product.html" {:product product
                                   :featured-products (db/get-featured-products)})
    (layout/error-page {:error-details "Requested product doesnt' exist."}))
  )

(defn checkout [_]
  (let [products (let [cart-items (-> _ :session :cart)]
                   (map (fn [item] {:id (-> item :product :id)
                                    :name (-> item :product :name)
                                    :quantity (:quantity item)
                                    :price (* (-> item :product :price) (:quantity item))}) cart-items))]
    (layout/view _ "checkout.html" {:products products :totalPrice (reduce + (map (fn [item] (:price item)) products))})))


(defn login [_]
  (layout/view _ "login.html" {}))
