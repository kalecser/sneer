(ns bazaar.templates
  "Bazaar enlive templates"
  (:use net.cgrand.enlive-html))

(deftemplate home "bazaar/templates/home.html"
  [product-list]
  [:#product] (clone-for [{:keys [name status]} product-list]
                         [:#status] (content (str status))
                         [:#name] (content name)))

