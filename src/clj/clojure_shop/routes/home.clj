(ns clojure-shop.routes.home
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.db.core :as db]
    [clojure-shop.middleware :as middleware]
    [ring.util.http-response :as response]
    [clojure_shop.handlers.register_user_handler :as register-user]
    [clojure_shop.handlers.login_user_handler :as login-user]
    [clojure-shop.session.helpers :as session]))

(defn get-categories []
  [{:id 0 :name "All"}
   {:id 1 :name "Shirts"}
   {:id 2 :name "Sports wears"}
   {:id 3 :name "Outwears"}])

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

(defn home-page [request]
  (layout/view request "home.html" {:categories (get-categories)
                                    :products (->
                                              (from-query request "categoryId")
                                              (get-products))}))

(defn register-page [_]
  (layout/view _ "register.html"))

(defn product-page [_]
  (if-let [product (get-product (from-query _ "productId"))]
    (layout/view _ "product.html" {:product product})
    (layout/error-page {:error-details "Requested product doesnt' exist."}))
  )

(defn checkout-page [_]
  (let [products (let [cart-items (-> _ :session :cart)]
                   (map (fn [item] {:id (-> item :product :id)
                                    :name (-> item :product :name)
                                    :quantity (:quantity item)
                                    :price (* (-> item :product :price) (:quantity item))}) cart-items))]
    (layout/view _ "checkout.html" {:products products
                                    :totalPrice (reduce + (map (fn [item] (:price item)) products))})
    ))


(defn login-page [_]
  (layout/view _ "login.html" {}))


(defn register [{:keys [params]}]
  (let 
    [errors (:errors (register-user/execute params))]
    (if errors (layout/render [params] "register.html" {:errors errors :attempted params}) 
               (layout/render [params] "successfull-registration.html" params))))

(defn handle-login [request] 
  (if-let 
    [user (login-user/execute (:username (:params request)) (:password (:params request)))]
      (session/add! :user user "/" request)
    (layout/view request "login.html" {:errors (conj [] "Invalid username or password.")})
  ))

(defn handle-add-to-cart [_]
  (if-let
    [product (get-product (Integer. (:productId (:params _))))]
    (session/append! :cart {:product product :quantity (Integer. (:quantity (:params _)))} (str "/products?productId=" (:id product)) _)
    (response/ok {:p (:productId (:params _))})
  ))

(defn handle-remove-from-cart [_])

(defn home-routes []
  ["" {:middleware [middleware/wrap-formats]}
                ["/"            {:get home-page}]

                ["/register"    {:get register-page
                                 :post register}]

                ["/product"     {:middleware [session/logged-in]
                                 :get product-page}]

                ["/products"    {:get product-page}]

                ["/checkout"    {:middleware [session/logged-in]
                                 :get checkout-page}]

                ["/login"       {:get login-page
                                 :post handle-login}]

                ["/add-to-cart" {:middleware [session/logged-in]
                                 :post handle-add-to-cart}]

                ["/remove-cart" {:middleware [session/logged-in]
                                 :get handle-remove-from-cart}]

                ["/logout"      {:get (fn [request] (session/remove! :user "/" request))}]
                ["/get-session" {:get (fn [request] (response/ok {:items (:session request)}))}]])






