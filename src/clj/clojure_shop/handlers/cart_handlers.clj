(ns clojure-shop.handlers.cart_handlers
  (:require [clojure-shop.session.helpers :as session]))



(def all-products [
                   {:id 1 :categoryId 1 :categoryName "All"          :name "Denim shirt" :isNew true :isBestSeller false :price 138 :image "https://mdbootstrap.com/img/Photos/Horizontal/E-commerce/Vertical/12.jpg" :description "Lorem ipsum dolor sit amet consectetur adipisicing elit. Et dolor suscipit libero eos atque quia ipsa sint voluptatibus! Beatae sit assumenda asperiores iure at maxime atque repellendus maiores quia sapiente."}
                   {:id 2 :categoryId 2 :categoryName "Shirts"       :name "Shirt"       :isNew false :isBestSeller false :price 48 :image "https://mdbootstrap.com/img/Photos/Horizontal/E-commerce/Vertical/13.jpg" :description "Lorem ipsum dolor sit amet consectetur adipisicing elit. Et dolor suscipit libero eos atque quia ipsa sint voluptatibus! Beatae sit assumenda asperiores iure at maxime atque repellendus maiores quia sapiente."}
                   {:id 3 :categoryId 3 :categoryName "Sports wears" :name  "Sweatshirt" :isNew false :isBestSeller true :price 14 :image "https://mdbootstrap.com/img/Photos/Horizontal/E-commerce/Vertical/14.jpg"  :description "Lorem ipsum dolor sit amet consectetur adipisicing elit. Et dolor suscipit libero eos atque quia ipsa sint voluptatibus! Beatae sit assumenda asperiores iure at maxime atque repellendus maiores quia sapiente."}
                   {:id 4 :categoryId 4 :categoryName "Outwears"     :name "Outwear"     :isNew true :isBestSeller false :price 223 :image "https://mdbootstrap.com/img/Photos/Horizontal/E-commerce/Vertical/15.jpg" :description "Lorem ipsum dolor sit amet consectetur adipisicing elit. Et dolor suscipit libero eos atque quia ipsa sint voluptatibus! Beatae sit assumenda asperiores iure at maxime atque repellendus maiores quia sapiente."}
                   ])

(defn from-query [request, param-name]
  (let [param (or (get (:query-params request) param-name))]
    (if param
      (if (number? (read-string param)) (Integer. param) param)
      param)))

(defn get-products
  [categoryId]
  (if (not= categoryId 0)
    (filter (fn [item] (= (:id item) categoryId)) all-products)
    all-products))

(defn get-product [productId]
  (first (filter (fn [el] (= (:id el) productId)) all-products)))

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
    [product (get-product (Integer. (:productId (:params _))))]
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