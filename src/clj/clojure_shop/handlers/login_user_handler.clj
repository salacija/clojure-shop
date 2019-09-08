(ns clojure_shop.handlers.login_user_handler
    (:require
        [clojure-shop.db.core :as db]
    ))


(defn execute [username password] 
  (db/login-user {:username username :password password}))
