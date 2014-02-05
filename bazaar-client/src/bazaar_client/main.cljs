(ns bazaar-client.main

  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [cljs.reader :as reader]
   [goog.net.XhrIo :as xhr]
   [cljs.core.async :as async])

  (:require-macros
   [cljs.core.async.macros :refer [go]]))

(defn GET [url]
  (let [ch (async/chan 1)
        callback #(let [res (-> % .-target .getResponseText)]
                    (go
                     (async/>! ch (reader/read-string res))
                     (async/close! ch)))]

    (xhr/send url callback "GET" nil (js-obj "Accept" "application/edn"))

    ch))

(defn my-products-view [products owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/ul nil
             (map #(dom/li nil (:name %)) products)))))

(defn $ [id]
  (. js/document getElementById id))

(def my-products (atom []))

(om/root my-products my-products-view ($ "my-products"))

(go
 (let [ch (GET "http://localhost:8080/api/my-products")]
   (when-let [products (async/<! ch)]
     (reset! my-products products))))
