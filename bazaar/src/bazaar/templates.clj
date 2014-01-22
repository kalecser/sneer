(ns bazaar.templates
  "Bazaar enlive templates"
  (:use net.cgrand.enlive-html))

(deftemplate home "bazaar/templates/home.html"
  [title]
  [:#title] (content title))
