(ns bazaar.clones

  (:require [bazaar.core :as core]
            [bazaar.git :as git]
            [clojure.core.async :as async :refer [<!! >!! thread close! alts!!]])

  (:import [org.eclipse.jgit.lib ProgressMonitor]))

(declare cloning-process-loop)

;(def process (start-cloning-process))
;(stop-cloning-process process)

(defn start-cloning-process []
  (let [process {:channel (async/chan)
                 :state (atom {})}] ; {<product-path> [<client-channel>]}}
    (thread
     (println "cloning process started.")
     (cloning-process-loop process)
     (println "cloning process finished."))
    process))

(defn stop-cloning-process [process]
  (close! (:channel process)))

(defn send-clone-request [process peer product response-channel]
  (>!! (:channel process) {:peer peer :product product :client response-channel}))

(defn active-clients-of [process product-path]
  (get @(:state process) product-path))

(defn- for-each-client-of [process product-path f]
  (doseq [client (active-clients-of process product-path)]
    (f client)))

(defn- notify-clients-of [process product-path & args]
  (for-each-client-of process product-path #(>!! % args)))

(defn- close-clients-of [process product-path]
  (for-each-client-of process product-path close!))

(defn- accept-clone-request [process {:keys [peer product client]}]
  (let [product-path (core/peer-product-path peer product)
        notify-clients (partial notify-clients-of process product-path)
        state (:state process)]
    (if-let [clients (active-clients-of process product-path)]
      (do
        (println "cloning of" product-path "already in progress, adding new client.")
        (swap! state assoc product-path (conj clients client))
        nil)
      (do
        (println "cloning of" product-path "started.")
        (swap! state assoc product-path [client])
        (thread
         (let [uri (format "git@github.com:%s/%s.git" peer product)

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
           (try
             (git/clone-with-progress-monitor pm uri product-path)
             (catch Exception e (notify-clients :error (.getMessage e))))
           product-path))))))

(defn- clone-finished [process product-path]
  (println "cloning of" product-path "finished.")
  (close-clients-of process product-path)
  (swap! (:state process) dissoc product-path))

(defn- cloning-process-loop [{:keys [channel] :as process}]
  (loop [channels #{channel}]
    (let [[v c] (alts!! (vec channels))]
      (if v
        (recur
         (if (identical? channel c)
           (if-let [new-thread (accept-clone-request process v)]
             (conj channels new-thread)
             channels)
           (do
             (clone-finished process v)
             channels)))

        (let [remaining-channels (disj channels c)]
          (if-not (empty? remaining-channels)
            (recur remaining-channels)))))))
