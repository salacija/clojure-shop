(ns clojure-shop.handlers.pages
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.db.core :as db]))

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

(defn from-query [request, param-name]
  (let [param (or (get (:query-params request) param-name))]
    (if param
      (if (number? (read-string param)) (Integer. param) param)
      param)))

(defn home [request]
  (layout/view request "home.html" {:categories (db/get-categories)
                                    :products (->
                                                (from-query request "categoryId")
                                                (get-products))}))
(defn register [_]
  (layout/view _ "register.html"))

(defn product [_]
  (if-let [product (get-product (from-query _ "productId"))]
    (layout/view _ "product.html" {:product product})
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
