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
                ["/orders"       {:middleware [session/logged-in]
                                  :get (fn [_] (layout/view _ "orders.html" {:orders (db/get-user-orders {:userId (:id (:user (:session _)))})}))}]

                ["/checkout"    {:middleware [session/logged-in]
                                 :get pages/checkout}]

                ["/profile"    {:middleware [session/logged-in]
                                 :get pages/profile}]

                ["/profile/edit"    {:middleware [session/logged-in]
                                     :get pages/edit-profile
                                     :post  (fn [_] (do (db/update-user! (assoc (:params _) :id (:id (:user (:session _))))) (redirect "/profile")))}]

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

                ["/place-order" {:middleware [session/logged-in]
                                 :post (fn [_] (let [cart (:cart (:session _))
                                                  order-form (:params _)
                                                  order-info (assoc order-form :userId (:id (:user (:session _))))
                                                  orderId  (:generated_key (db/create-order order-info))
                                                  order-lines (map (fn [cart-item] {:productName (:name (:product cart-item))
                                                                                    :quantity (:quantity cart-item)
                                                                                    :price (:price (:product cart-item))
                                                                                    :orderId orderId}) cart)]
                                                 (do (doseq [order-line order-lines] (db/create-order-line order-line))
                                                     (postal.core/send-message {:host "smtp.gmail.com"
                                                                                :user "emailforclojure@gmail.com"
                                                                                :pass "clojuretest1"
                                                                                :tls true
                                                                                :port 587}
                                                                               {:from "emailforclojure@gmail.com",
                                                                                :to (:email order-info)
                                                                                :subject "Order placed"
                                                                                :body [
                                                                                       {:type "text/html"
                                                                                        :content (selmer.parser/render-file "email.html" {:order-info order-info
                                                                                                                                          :order-lines order-lines})}]})
                                                     (session/remove! :cart "/order-placed" _))))}]
                ["/order-placed" {:middleware [session/logged-in]
                                  :get (fn [_] (layout/view _ "order-placed.html" (let [userId (:id (:user (:session _)))
                                                                                        order (db/get-last-user-order-info {:userId userId})]
                                                                                    {:order order})))}]
                ["/admin"       {:middleware [session/admin]
                                 :get (fn [_] (layout/view _ "admin.html"))}]

                ["/admin/orders" {:middleware [session/admin]
                                  :get (fn [_] (layout/view _ "orders.html" {:orders (db/get-all-orders)}))}]

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

                ["/check-username" {:get (fn [_] (response/ok (let [user (:user (:session _))]
                                                                      (db/username-already-in-use {:userId (:id user)
                                                                                                   :username (:username (:params _))}))))}]
               ["/check-email" {:get (fn [_] (response/ok (let [user (:user (:session _))]
                                                               (db/email-already-in-use {:userId (:id user)
                                                                                            :email (:email (:params _))}))))}]
   ])





