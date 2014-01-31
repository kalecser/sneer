(ns bazaar.main
  (:gen-class)

  (:require [bazaar.core :as core]
            [bazaar.templates :as templates]
            [bazaar.clones :as clones]
            [org.httpkit.server :as http-kit :refer [run-server with-channel]]
            [clojure.core.async :as async]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.browse :refer [browse-url]]))

(def state nil)

(defn show-page [peer-products]
  (templates/recompile-home)
  {:headers {"Content-Type" "text/html;charset=utf8"}
   :body (templates/home (core/product-list) peer-products)})

(defn show-home [ _ ]
  (show-page []))

(defn show-products [peer-login]
  (show-page (core/peer-product-list peer-login)))

(defn run-peer-product [peer product req]
  (with-channel req http-channel
    (let [response-channel (async/chan 1)]
      (http-kit/on-close http-channel (fn [_] (async/close! response-channel)))
      (clones/send-clone-request (:cloning-process state) peer product response-channel)
      (async/go
       (loop []
         (when-let [response (async/<! response-channel)]
           (http-kit/send! http-channel (str response) false)
           (recur)))
       (http-kit/close http-channel)))))

(defroutes web-app
  (GET "/" [] show-home)
  (GET "/products" [peer] (show-products peer))
  (GET "/products/:peer/:product/run"
       [peer product]
       (partial run-peer-product peer product))
  (route/files "/static" {:root (str (System/getProperty "user.dir") "/static")}))

(defn start []
  (let [port 8080]
    (def state {:port port
                :server-closer (run-server (handler/site #'web-app) {:port port})
                :cloning-process (clones/start-cloning-process)})
    state))

(defn stop []
  (if-let [{:keys [server-closer cloning-process]} state]
    (try
      (server-closer)
      (clones/stop-cloning-process cloning-process)
      (finally (def state nil)))))

;(start)
;(stop)
;http://localhost:8080/

(defn -main [& args]
  (let [state (start)]
    (browse-url (str "http://localhost:" (:port state)))))
