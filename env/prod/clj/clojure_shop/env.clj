(ns clojure-shop.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clojure-shop started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clojure-shop has shut down successfully]=-"))
   :middleware identity})
