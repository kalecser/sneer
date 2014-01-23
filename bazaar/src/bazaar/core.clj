(ns bazaar.core
  (:require [clojure.java.io :as io]))

(def products-root
  (str (System/getProperty "user.home") "/sneer/products"))

(defn product-list []
  (let [product-folders (-> products-root io/file .listFiles)]
    (for [folder product-folders :when (.isDirectory folder)]
      {:status :new, :name (.getName folder)})))

(product-list)

;  [{:status :new, :name "Javatari 2.0"}
;   {:status :modified, :name "Emacs for Clojure"}])
