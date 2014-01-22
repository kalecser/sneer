(ns bazaar.main
  (:gen-class)
  (:require clojure.java.browse)
  (:use org.httpkit.server)
  (:require [bazaar.templates :as templates]))

(defn web-app [request]
  {:status 200
   :headers {"Content-Type" "text/html;charset=utf8"}
   :body (templates/home "Parabéns!!!")})

(defn start-http-server []
  (let [port 8080]
    (run-server #'web-app {:port port})
    port))

;(def server (run-server #'web-app {:port 8080}))
;(server)

(defn open-browser [url]
  (clojure.java.browse/browse-url url))

(defn -main [& args]
  (let [port (start-http-server)]
    (open-browser (str "http://localhost:" port))))
