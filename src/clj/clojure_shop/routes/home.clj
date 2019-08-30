(ns clojure-shop.routes.home
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.db.core :as db]
    [clojure.java.io :as io]
    [clojure-shop.middleware :as middleware]
    [ring.util.http-response :as response]
    [clojure_shop.handlers.register_user_handler :as register-user]
    [struct.core :as st]))

(defn home-page [_]
  (layout/render _ "home.html"))

(defn register-page [_]
  (layout/render _ "register.html"))

(defn product-page [_]
  (layout/render _ "product.html"))

(defn checkout-page [_]
  (layout/render _ "checkout.html"))


(defn register [{:keys [params]}]
  (let 
    [errors (:errors (register-user/execute params))]
    (if errors (response/bad-request errors) (layout/render [params] "successfull-registration.html" params))
  ))

(defn home-routes []
  ["" {:middleware [middleware/wrap-formats]}
  ["/" {:get home-page}]
   ["/register" {:get register-page
                 :post register}]
  ["/products/:id" {:get product-page}]
  ["/checkout" {:get checkout-page}]
  ])


