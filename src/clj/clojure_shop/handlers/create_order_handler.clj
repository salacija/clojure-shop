(ns clojure-shop.handlers.create-order-handler
  (:require
    [clojure-shop.db.core :as db]))

(defn execute [order-info order-items]
  (let [orderId (:generated_key (db/create-order order-info))]
    (doseq [item order-items] (db/create-order-line (assoc item :orderId orderId)))))