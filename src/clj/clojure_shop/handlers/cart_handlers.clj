(ns clojure-shop.handlers.cart_handlers
  (:require [clojure-shop.session.helpers :as session]
            [clojure-shop.db.core :as db]))

(defn from-query [request, param-name]
  (let [param (or (get (:query-params request) param-name))]
    (if param
      (if (number? (read-string param)) (Integer. param) param)
      param)))

(defn product-exists-in-session [product request]
  (let [products-in-session (-> request :session :cart)
        ids-in-session (map (fn [p] (-> p :product :id)) products-in-session)]
    (not (empty? (filter (fn [id] (= id (:id product))) ids-in-session)))
    ))

(defn update-product-quantity [productId request]
  (let [product-from-session (first  (filter (fn [item] (= productId (-> item :product :id))) (-> request :session :cart )))]
    (update product-from-session :quantity + (Integer. (-> request :params :quantity)))
    ))

(defn add [_]
  (if-let
    [product (db/get-product {:productId (Integer. (:productId (:params _)))})]
    (if
      (product-exists-in-session product _)
      (let [rest-of-the-products (filter (fn [item] (not= (Integer. (:id product)) (-> item :product :id))) (-> _ :session :cart))
            product-id (Integer. (:productId (:params _)))
            updated (update-product-quantity product-id _)]
        (session/add! :cart (conj rest-of-the-products updated) (str "/products?productId=" (:id product)) _)
        )
      (session/append! :cart {:product product :quantity (Integer. (:quantity (:params _)))} (str "/products?productId=" (:id product)) _))))

(defn remove [_]
  (let [filtered-cart (filter (fn [item] (not= (from-query _ "productId") (-> item :product :id))) (-> _ :session :cart))]
    (session/add! :cart filtered-cart "/checkout" _)))