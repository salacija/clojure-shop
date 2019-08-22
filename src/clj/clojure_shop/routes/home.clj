(ns clojure-shop.routes.home
  (:require
    [clojure-shop.layout :as layout]
    [clojure-shop.db.core :as db]
    [clojure.java.io :as io]
    [clojure-shop.middleware :as middleware]
    [ring.util.http-response :as response]))

(defn home-page [_]
  (layout/render _ "home.html"))

(defn product-page [_]
  (layout/render _ "product.html"))

(defn checkout-page [_]
  (layout/render _ "checkout.html"))

(defn home-routes []
  ["" {:middleware [middleware/wrap-formats]}
  ["/" {:get home-page}]
  ["/products/:id" {:get product-page}]
  ["/checkout" {:get checkout-page}]
  ])