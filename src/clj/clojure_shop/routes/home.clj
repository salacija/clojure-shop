(ns clojure-shop.routes.home
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.middleware :as middleware]
    [clojure-shop.db.core :as db]
    [ring.util.http-response :as response]
    [clojure_shop.handlers.register_user_handler :as register-user]
    [clojure_shop.handlers.login_user_handler :as login-user]
    [clojure_shop.handlers.admin_handlers :as admin]
    [clojure-shop.session.helpers :as session]
    [clojure-shop.handlers.pages :as pages]
    [clojure-shop.handlers.cart_handlers :as cart]
    [ring.util.response :refer [redirect file-response]])
  )

(defn register [{:keys [params]}]
  (let 
    [errors (:errors (register-user/execute params))]
    (if errors (layout/render [params] "register.html" {:errors errors :attempted params}) 
               (layout/render [params] "successfull-registration.html" params))))

(defn place-order-handler [_]
  (session/remove! :cart "/order-placed" _))

(defn home-routes []
  ["" {:middleware [middleware/wrap-formats]}
                ["/"            {:get pages/home}]

                ["/register"    {:get pages/register
                                 :post register}]

                ["/products"     {:middleware [session/logged-in]
                                 :get pages/product}]

                ["/checkout"    {:middleware [session/logged-in]
                                 :get pages/checkout}]

                ["/login"       {:get pages/login
                                 :post (fn [_] (let [username (:username (:params _))
                                                     password (:password (:params _))]
                                                 (login-user/execute _ username password)))}]

                ["/add-to-cart" {:middleware [session/logged-in]
                                 :post cart/add}]

                ["/remove-cart" {:middleware [session/logged-in]
                                 :get cart/remove}]

                ["/logout"      {:get (fn [_]
                                        (session/destroy! "/"))}]

                ["/place-order" {:post place-order-handler}]

                ["/admin"       {:middleware [session/admin]
                                 :get (fn [_] (layout/view _ "admin.html"))}]

                ["/admin/orders" {:middleware [session/admin]
                                  :get (fn [_] (layout/view _ "orders.html"))}]

                ["/admin/categories" {:middleware [session/admin]
                                      :get (fn [_] (layout/view _ "categories.html" {:categories (db/get-categories)}))
                                      :post admin/add-category }]

                ["/admin/categories/delete/:id" {:middleware [session/admin]
                                                 :get (fn [_] (admin/delete _ db/delete-category! "categories"))}]
                ["/admin/categories/update/:id"  {:middleware [session/admin]
                                                 :get (fn [_] (layout/view _ "update-category.html" {:category (db/get-category {:id (:id (:path-params _))})}))
                                                 :post admin/update-category}]

                ["/admin/products" {:middleware [session/admin]
                                    :get (fn [_] (layout/view _ "admin-products.html" {:products (db/get-products)}))
                                    :post admin/add-product}]

                ["/admin/products/create" {:middleware [session/admin]
                                          :get (fn [_] (layout/view _ "create-product.html" {:categories (db/get-categories)}))}]

                ["/admin/products/update/:id" {:middleware [session/admin]
                                              :get (fn [_] (layout/view _ "update-product.html" { :product (db/get-product {:productId (:id (:path-params _))})
                                                                                             :categories (db/get-categories)}))
                                              :post admin/update-product}]
                ["/admin/products/delete/:id" {:middleware [session/admin]
                                                :get (fn [_] (admin/delete _ db/delete-product! "products"))}]

                ["/order-placed" {:get (fn [_] (layout/view _ "order-placed.html"))}]

                ["/get-session" {:get (fn [request] (response/ok {:items (:session request)}))}]])





