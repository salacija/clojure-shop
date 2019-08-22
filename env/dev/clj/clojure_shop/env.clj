(ns clojure-shop.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [clojure-shop.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clojure-shop started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clojure-shop has shut down successfully]=-"))
   :middleware wrap-dev})
