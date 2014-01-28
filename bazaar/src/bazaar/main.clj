(ns bazaar.main
  (:gen-class)
  (:require [bazaar.core :as core])
  (:require clojure.java.browse)
  (:use org.httpkit.server)
  (:require [bazaar.templates :as templates])
  (:require [compojure.core :as compojure])
  (:require [compojure.handler :as handler])
  (:require [compojure.route :as route]))

(defn show-landing-page [ _ ]
  (templates/recompile-home)
  {
   ;:status 200
   ;:headers {"Content-Type" "text/html;charset=utf8"}
   :body (templates/home (core/product-list) [])})

(defn show-products [peer-login]
  (templates/recompile-home)
  {:status 200
   :headers {"Content-Type" "text/html;charset=utf8"}
   :body (templates/home (core/product-list) (core/peer-product-list peer-login))})

(compojure/defroutes web-app
  (compojure/GET "/" [] show-landing-page)
  (compojure/GET "/products" [peer] (show-products peer))
  (route/files "/static" {:root (str (System/getProperty "user.dir") "/static")}))


(defn start-http-server []
  (let [port 8080]
    (run-server (handler/site #'web-app) {:port port})
    port))

;(def server-closer (run-server (handler/site #'web-app) {:port 8080}))
;(server-closer)

(defn open-browser [url]
  (clojure.java.browse/browse-url url))

(defn -main [& args]
  (let [port (start-http-server)]
    (open-browser (str "http://localhost:" port))))
