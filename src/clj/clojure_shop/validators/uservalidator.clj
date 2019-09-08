(ns clojure_shop.validators.uservalidator
    (:require
        [struct.core :as st]
        [clojure-shop.db.core :as db]
    ))

(def first-name-regex (re-pattern "^[A-Z][a-z]{3,13}$"))


(defn req [param-name] (str param-name " is required."))
(defn format [param-name] (str "Invalid " param-name " format."))
(defn length [param-name length] (str  param-name " must be at least " length " characters long."))

(def unique-username
    {:message "Username is already in use."
     :optional false
     :validate (fn [v] (not (db/get-user-by-username {:username v})) )} )

(def unique-email
    {:message "Email is already in use."
    :optional false
    :validate (fn [v] (not (db/get-user-by-email {:email v})) )})

(def first-name-format
    {:message "Invalid firstname format."
    :optional false
    :validate (fn [v] (re-matches first-name-regex v))})

(def user-validator
    {:firstName [[st/required :message (req "First name")] 
                 [first-name-format :message (format "First name")]]
     :lastName  [[st/required :message (req "Last name")]
                 [first-name-format :message (format "Last name")]]
     :email     [[st/required :message (req "Email")] 
                 [st/email :message (format "Email")] 
                 [unique-email]]
     :password  [[st/required :message (req "Password")] 
                 [st/min-count 6 :message (length "Password" 6)]]
     :username  [[st/required :message (req "Username")]
                 [st/min-count 4 :message (length "Username" 4)] 
                 [unique-username]]
})

(defn validate-user [params]
    (st/validate params user-validator))