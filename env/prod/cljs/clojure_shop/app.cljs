(ns clojure-shop.app
  (:require [clojure-shop.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
