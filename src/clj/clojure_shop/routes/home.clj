(ns clojure-shop.routes.home
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.middleware :as middleware]
    [clojure-shop.db.core :as db]
    [ring.util.http-response :as response]
    [ring.util.response :as ring-response]
    [clojure_shop.handlers.register_user_handler :as register-user]
    [clojure_shop.handlers.login_user_handler :as login-user]
    [clojure_shop.handlers.admin_handlers :as admin]
    [clojure-shop.session.helpers :as session]
    [ring.util.response :refer [redirect file-response]])
  )

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
  (layout/view request "home.html" {:categories (db/get-categories)
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
    (layout/view _ "checkout.html" {:products products :totalPrice (reduce + (map (fn [item] (:price item)) products))})))


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

(defn product-exists-in-session [product request]
  (let [products-in-session (-> request :session :cart)
        ids-in-session (map (fn [p] (-> p :product :id)) products-in-session)]
    (not (empty? (filter (fn [id] (= id (:id product))) ids-in-session)))
    ))

(defn update-product-quantity [productId request]
  (let [product-from-session (first  (filter (fn [item] (= productId (-> item :product :id))) (-> request :session :cart )))]
    (update product-from-session :quantity + (Integer. (-> request :params :quantity)))
    ))

(defn handle-add-to-cart [_]
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

(defn handle-remove-from-cart [_]
  (let [filtered-cart (filter (fn [item] (not= (from-query _ "productId") (-> item :product :id))) (-> _ :session :cart))]
    (session/add! :cart filtered-cart "/checkout" _)))

(defn place-order-handler [_]
  (let [shipping-info (:params _)
        cart-items (-> _ :session :cart)])
  (session/remove! :cart "/order-placed" _))

(defn add-category-handler [_]
  (if-let [success (db/create-category! (:params _))]
    (ring-response/redirect "/admin/categories")
    (ring-response/redirect "ERROR")))


(defn handle-update-category [_]
  (if-let [success (db/update-category! (:params _))]
    (ring-response/redirect "/admin/categories")
    (ring-response/redirect "error")))

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

                ["/logout"      {:get (fn [_]
                                        (session/destroy! "/"))}]

                ["/place-order" {:post place-order-handler}]

                ["/admin"       {:middleware [session/admin]
                                 :get (fn [_] (layout/view _ "admin.html"))}]

                ["/admin/orders" {:middleware [session/admin]
                                  :get (fn [_] (layout/view _ "orders.html"))}]

                ["/admin/categories" {:middleware [session/admin]
                                      :get (fn [_] (layout/view _ "categories.html" {:categories (db/get-categories)}))
                                      :post add-category-handler }]

                ["/admin/categories/delete/:id" {:middleware [session/admin]
                                                 :get (fn [_] (admin/delete _ db/delete-category! "categories"))}]
                ["/admin/categories/update/:id"  {:middleware [session/admin]
                                                 :get (fn [_] (layout/view _ "update-category.html" {:category (db/get-category {:id (:id (:path-params _))})}))
                                                 :post handle-update-category}]

                ["/admin/products" {:middleware [session/admin]
                                    :get (fn [_] (layout/view _ "admin-products.html" {:products (db/get-products)}))
                                    :post admin/add-product}]

                ["/admin/products/create" {:middleware [session/admin]
                                          :get (fn [_] (layout/view _ "create-product.html" {:categories (db/get-categories)}))}]

                ["/admin/products/update/:id" {:middleware [session/admin]
                                              :get (fn [_] (layout/view _ "update-product.html" { :product (db/get-product {:id (:id (:path-params _))})
                                                                                             :categories (db/get-categories)}))
                                              :post admin/update-product}]
                ["/admin/products/delete/:id" {:middleware [session/admin]
                                                :get (fn [_] (admin/delete _ db/delete-product! "products"))}]

                ["/order-placed" {:get (fn [_] (layout/view _ "order-placed.html"))}]

                ["/get-session" {:get (fn [request] (response/ok {:items (:session request)}))}]])





