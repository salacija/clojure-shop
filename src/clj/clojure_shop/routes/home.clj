(ns clojure-shop.routes.home
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.db.core :as db]
    [clojure.java.io :as io]
    [clojure-shop.middleware :as middleware]
    [ring.util.http-response :as response]
    [struct.core :as st]
    [clojure_shop.validators.uservalidator :as userval]))

(defn home-page [_]
  (layout/render _ "home.html"))

(defn register-page [_]
  (layout/render _ "register.html"))

(defn product-page [_]
  (layout/render _ "product.html"))

(defn checkout-page [_]
  (layout/render _ "checkout.html"))

(defn handle-registration [request] {:status 200 :body "POST request"})

(defn handler [_]
  {:status 200, :body "ok"})


(defn register-user-handler [{:keys [params]}]
  (response/ok
    (let 
      [
        val-result (userval/validate-user params) 
        errors (first val-result) 
        data (second val-result)
      ]
      (if errors errors data))))

(defn home-routes []
  ["" {:middleware [middleware/wrap-formats]}
  ["/" {:get home-page}]
   ["/register" {:get register-page
                 :post register-user-handler}]
  ["/products/:id" {:get product-page}]
  ["/checkout" {:get checkout-page}]
  ])


