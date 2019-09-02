(ns clojure_shop.handlers.login_user_handler
    (:require
        [postal.core :as email]
        [clojure-shop.db.core :as db]
        [clojure.java.io :as io]
        [clojure-shop.middleware :as middleware]
    ))


(defn execute [username password] 
    (if (= username "luke") 
        
        {:username username 
         :firstName "Luka"
         :lastName "Lukic"
         :id 1
         :isAdmin true
         :email "luka.lukic@ict.edu.rs"}
        
         nil))
