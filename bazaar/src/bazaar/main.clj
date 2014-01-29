(ns bazaar.main
  (:gen-class)

  (:require [bazaar.core :as core]
            [bazaar.templates :as templates]
            [bazaar.clones :as clones]
            [org.httpkit.server :as http-kit :refer [run-server with-channel]]
            [compojure.core :as compojure :refer [GET]]
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

(defn run-peer-product [peer product req]
  (with-channel req channel
    (clones/start-async-clone peer product channel)))

(compojure/defroutes web-app
  (GET "/" [] show-home)
  (GET "/products" [peer] (show-products peer))
  (GET "/products/:peer/:product/run" [peer product] (partial run-peer-product peer product))
  (route/files "/static" {:root (str (System/getProperty "user.dir") "/static")}))

(defn start-http-server []
  (let [port 8080]
    (run-server (handler/site #'web-app) {:port port})
    port))

;(def server-closer (run-server (handler/site #'web-app) {:port 8080}))
;(server-closer)
;http://localhost:8080/

(defn -main [& args]
  (let [port (start-http-server)]
    (clones/start-cloning-process)
    (browse-url (str "http://localhost:" port))))
