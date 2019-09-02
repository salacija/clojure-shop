(ns clojure-shop.session.helpers
    (:require
      [ring.util.response :as ring-response]))

(defn add! 
"Used to set item in session and perform the redirection to the expected page."
[key item redirect-to {session :session}]
(-> (ring-response/redirect redirect-to)
    (assoc :session (assoc session key item))))

(defn append!
  "Used to add item into the session collection."
  [key item redirect-to {session :session}]
  (-> (ring-response/redirect redirect-to)
      (assoc :session (assoc session key (conj (get session key) item)))))

(defn remove!
"Used to set remove item (keyword) from session and perform the redirection to the expected page."
[key redirect-to {session :session}]
(-> (ring-response/redirect redirect-to)
    (assoc :session (dissoc session key))))

(defn logged-in [handler]
  (fn [request]
    (if-not (:user (:session request)) (ring-response/redirect "/") (handler request))))
