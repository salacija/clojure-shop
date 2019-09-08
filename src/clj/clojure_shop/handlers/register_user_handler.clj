(ns clojure_shop.handlers.register_user_handler
    (:require
        [clojure_shop.validators.uservalidator :as val]
        [ring.util.http-response :as response]
        [postal.core :as email]
        [clojure-shop.layout :as layout]
        [clojure-shop.db.core :as db]
        [clojure.java.io :as io]
        [clojure-shop.middleware :as middleware]
    ))

(defn send-registration-email  
    ([user]
        (email/send-message {:host "smtp.gmail.com"
                            :user "lukeelukic@gmail.com"
                            :pass "Teamlukic14"
                            :tls true
                            :port 587}
                            {:from "lukeelukic@gmail.com",
                            :to (:email user)
                            :subject "Registration"
                            :body "You have successfully registered to our website!"}))
    ([user test]
        (print "fake impl")))

(defn execute [user]
    (let
        [val-result (val/validate-user user)
         errors (first val-result)]
        (if errors 
            {:errors (vals errors)} 
            (do
                (if (db/create-user! user)
                    (send-registration-email user "test"))))))

