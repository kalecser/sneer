(ns bazaar.core
  (:require [clojure.java.io :as io])
  (:use clj-jgit.porcelain))

(def products-root
  (str (System/getProperty "user.home") "/sneer/products"))

(defn product-folders []
  (filter #(.isDirectory %) (-> products-root io/file .listFiles)))

(defn is-git [folder]
  (.exists (java.io.File. folder ".git")))

(defn status [folder]
  (if (is-git folder)
    :shared
    :new))

(defn product-list []
  (map #(hash-map :name (.getName %) :status (status %)) (product-folders)))

(product-list)

;  [{:status :new, :name "Javatari 2.0"}
;   {:status :modified, :name "Emacs for Clojure"}])
