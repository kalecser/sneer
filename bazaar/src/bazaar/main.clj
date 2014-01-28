(ns bazaar.main
  (:gen-class)
  (:require [bazaar.core :as core]
            [bazaar.templates :as templates]
            [org.httpkit.server :as httpkit]
            [compojure.core :as compojure]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.browse :refer [browse-url]]))

(defn show-page [peer-products]
  (templates/recompile-home)
  {:headers {"Content-Type" "text/html;charset=utf8"}
   :body (templates/home (core/product-list) peer-products)})

(defn show-home [ _ ]
  (show-page []))

(defn show-products [peer-login]
  (show-page (core/peer-product-list peer-login)))

(compojure/defroutes web-app
  (compojure/GET "/" [] show-home)
  (compojure/GET "/products" [peer] (show-products peer))
  (route/files "/static" {:root (str (System/getProperty "user.dir") "/static")}))

(defn start-http-server []
  (let [port 8080]
    (httpkit/run-server (handler/site #'web-app) {:port port})
    port))

;(def server-closer (httpkit/run-server (handler/site #'web-app) {:port 8080}))
;(server-closer)

(defn -main [& args]
  (let [port (start-http-server)]
    (browse-url (str "http://localhost:" port))))
