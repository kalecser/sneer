(ns bazaar.templates
  "Bazaar enlive templates"
  (:use net.cgrand.enlive-html)
  (:require [clojure.java.io :as io]))

(deftemplate home (io/resource "public/home.html")
  [product-list peer-products]
  [:#product] (clone-for [{:keys [name status]} product-list]
                         [:#status] (content (str status))
                         [:#name] (content name))
  [:#peer-product] (clone-for [{:keys [name status peer]} peer-products]
                              [:#status] (content (str status))
                              [:#name] (content name)
                              [:a] (set-attr :href (str "/products/" peer "/" name "/run"))))
