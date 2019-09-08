(ns clojure-shop.helpers.uploads
  (:require [clojure.java.io :as io]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn upload-handler [{:keys [filename tempfile]}]
  (io/copy (io/file tempfile) (io/file "resources" "public" "img" filename))
  (io/delete-file (.getAbsolutePath tempfile))
  (response/ok {:status :ok}))
