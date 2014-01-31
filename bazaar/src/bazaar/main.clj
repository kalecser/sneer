(ns bazaar.main
  (:gen-class)

  (:require [bazaar.core :as core]
            [bazaar.templates :as templates]
            [bazaar.clones :as clones]
            [org.httpkit.server :as http-kit :refer [run-server]]
            [clojure.core.async :as async]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.browse :refer [browse-url]]))

(def state (atom nil))

(defn show-page [peer-products]
  {:headers {"Content-Type" "text/html;charset=utf8"}
   :body (templates/home (core/product-list) peer-products)})

(defn show-home [ _ ]
  (show-page []))

(defn show-products [peer-login]
  (show-page (core/peer-product-list peer-login)))

(defn run-peer-product [peer product req]
  (http-kit/with-channel req http-channel
    (let [response-channel (async/chan 1)]
      (http-kit/on-close http-channel (fn [_] (async/close! response-channel)))
      (clones/serve-clone-request (:cloning-process @state) peer product response-channel)
      (async/go
       (loop []
         (when-let [response (async/<! response-channel)]
           (http-kit/send! http-channel (str response) false) ; false means dont close
           (recur)))
       (http-kit/close http-channel)))))

(defroutes web-app
  (GET "/" [] show-home)
  (GET "/products" [peer] (show-products peer))
  (GET "/products/:peer/:product/run"
       [peer product]
       (partial run-peer-product peer product))
  (route/resources "/"))
(defn force-reload [app reloadables]
  (fn [req]
    (doseq [ns-sym reloadables]
      (require ns-sym :reload))
    (app req)))

(defn start-server [port]
  (let [app (-> #'web-app (force-reload '[bazaar.templates]) handler/site)]
    {:port port
     :server-closer (run-server app {:port port})
     :cloning-process (clones/start-cloning-process)}))

(defn start []
  (let [port 8080]
    (swap! state #(do
      (assert (nil? %))
      (start-server port)))))

(defn stop []
  (if-let [{:keys [server-closer cloning-process] :as old} @state]
    (try
      (server-closer)
      (clones/stop-cloning-process cloning-process)
      (finally
        (swap! state #(if-not (identical? % old) % nil))))))

;(start)
;(stop)
;http://localhost:8080/

(defn -main [& args]
  (let [state (start)]
    (browse-url (str "http://localhost:" (:port state)))))
