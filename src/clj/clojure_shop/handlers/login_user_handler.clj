(ns clojure_shop.handlers.login_user_handler
    (:require
        [clojure-shop.db.core :as db]
        [clojure-shop.session.helpers :as session]
        [clojure-shop.layout :as layout]
    ))


(defn execute [_ username password]
  (if-let [user (db/login-user {:username username :password password})]
    (session/add! :user user "/" _)
    (layout/view _ "login.html" {:errors (conj [] "Invalid username or password.")})))

