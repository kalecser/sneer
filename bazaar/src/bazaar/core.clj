(ns bazaar.core
  (:require [clojure.java.io :as io])
  (:use clj-jgit.porcelain))

(def products-root
  (str (System/getProperty "user.home") "/sneer/products"))

(defn list-subfolders [^String folder-name]
  (filter #(.isDirectory %) (-> folder-name io/file .listFiles)))

(defn product-folders []
  (list-subfolders products-root))

(defn is-git [folder]
  (.exists (java.io.File. folder ".git")))

(defn status [folder]
  (if (is-git folder)
    :shared
    :new))

(defn product-list []
  (map #(hash-map :name (.getName %) :status (status %)) (product-folders)))

(defn peer-product-list [peer-login]
  [{:status :new, :name "Javatari 2.0"}
   {:status :forked, :name (str "Emacs " peer-login)}])


(product-list)

;  [{:status :new, :name "Javatari 2.0"}
;   {:status :modified, :name "Emacs for Clojure"}])
