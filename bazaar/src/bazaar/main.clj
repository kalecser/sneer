(ns bazaar.main
  (:gen-class)
  (:require [bazaar.core :as core])
  (:require clojure.java.browse)
  (:require [org.httpkit.server :as httpkit])
  (:require [bazaar.templates :as templates])
  (:require [compojure.core :as compojure])
  (:require [compojure.handler :as handler])
  (:require [compojure.route :as route]))

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

(defn open-browser [url]
  (clojure.java.browse/browse-url url))

(defn -main [& args]
  (let [port (start-http-server)]
    (open-browser (str "http://localhost:" port))))
