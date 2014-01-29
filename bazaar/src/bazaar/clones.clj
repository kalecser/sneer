(ns bazaar.clones

  (:require [bazaar.core :as core]
            [bazaar.git :as git]
            [org.httpkit.server :as http-kit]
            [clojure.core.async :as async :refer [<!! >!! thread alts!!]])

  (:import [org.eclipse.jgit.lib ProgressMonitor]))

(def ^:private clone-request-channel (async/chan))

(def ^:private active-clones
  "{<product-path> [<client>]}"
  (atom {}))

(defn start-async-clone [peer product client]
  (>!! clone-request-channel {:peer peer :product product :client client}))

(defn get-active-clients [product-path]
  (get @active-clones product-path))

(defn notify-clients [product-path & args]
  (doseq [c (get-active-clients product-path)]
    (http-kit/send! c (str args) false)))

(defn accept-clone-request [{:keys [peer product client]}]
  (let [product-path (core/peer-product-path peer product)]
    (if-let [clients (get-active-clients product-path)]
      (do
        (println "cloning of " product-path " already in progress, adding new client.")
        (swap! active-clones assoc product-path (conj clients client))
        nil)
      (do
        (println "cloning of " product-path "started.")
        (swap! active-clones assoc product-path [client])
        (thread
         (let [uri (format "git@github.com:%s/%s.git" peer product)
               notify-clients (partial notify-clients product-path)
               pm (reify ProgressMonitor
                    (start [this totalTasks]
                      (notify-clients :start totalTasks))
                    (update [this completed]
                      (notify-clients :update completed))
                    (beginTask [this title totalWork]
                      (notify-clients :beginTask title totalWork))
                    (endTask [this]
                      (notify-clients :endTask))
                    (isCancelled [this]
                      false))]
           (git/clone-with-progress-monitor pm uri product-path)
           product-path))))))

(defn clone-finished [product-path]
  (println "clone of " product-path " has finished.")
  (doseq [client (get-active-clients product-path)]
    (http-kit/close client))
  (swap! active-clones dissoc product-path))

(defn start-cloning-process []
  (thread
   (println "cloning process started.")

   (loop [channels #{clone-request-channel}]
     (let [[v c] (async/alts!! (vec channels))]

       (println (format "value %s from %s (%d active)" v c (count channels)))
       (if v
         (recur
          (if (identical? clone-request-channel c)
            (if-let [new-thread (accept-clone-request v)]
              (conj channels new-thread)
              channels)
            (do
              (clone-finished v)
              channels)))

         (let [remaining-channels (disj channels c)]
           (if-not (empty? remaining-channels)
             (recur remaining-channels))))))

   (println "cloning process finished.")))

(defn stop-cloning-process []
  (async/close! clone-request-channel))
;
;(start-cloning-process)
;(stop-cloning-process)
