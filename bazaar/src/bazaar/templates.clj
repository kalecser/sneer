(ns bazaar.templates
  "Bazaar enlive templates"
  (:use net.cgrand.enlive-html))

(defn recompile-home []
  (deftemplate home "bazaar/templates/home.html"
    [product-list peer-products]
    [:#product] (clone-for [{:keys [name status]} product-list]
                           [:#status] (content (str status))
                           [:#name] (content name))
    [:#peer-product] (clone-for [{:keys [name status]} peer-products]
                                [:#status] (content (str status))
                                [:#name] (content name))))
