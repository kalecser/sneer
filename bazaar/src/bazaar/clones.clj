(ns bazaar.clones
  (:require   [bazaar.core :as core]
              [clojure.core.async :as async]
              [clojure.java.io :as io]
              [clojure.core.async :as async :refer [chan >!! <!! alts!! timeout thread close!]])
  (:import [org.eclipse.jgit.api Git]
           [org.eclipse.jgit.lib EmptyProgressMonitor]))


(defn clone-with-progress-monitor
  ([pm uri local-dir]
   (clone-with-progress-monitor pm uri local-dir "origin" "master" false))
  ([pm uri local-dir remote-name]
   (clone-with-progress-monitor pm uri local-dir remote-name "master" false))
  ([pm uri local-dir remote-name local-branch]
   (clone-with-progress-monitor pm uri local-dir remote-name local-branch false))
  ([pm uri local-dir remote-name local-branch bare?]
   (-> (Git/cloneRepository)
       (.setURI uri)
       (.setDirectory (io/as-file local-dir))
       (.setRemote remote-name)
       (.setBranch local-branch)
       (.setBare bare?)
       (.setProgressMonitor pm)
       (.call))))

(defn simple-monitor [monitor-channel]
  (proxy [EmptyProgressMonitor] []
    (beginTask [taskName _]
               (>!! monitor-channel taskName))
    (update [_]
              (>!! monitor-channel " ."))
    (endTask []
               (>!! monitor-channel "\n"))
    (isCancelled []
                 false)))

(defn clone-with-simple-monitor [uri local-dir monitor-channel]
  (try
    (clone-with-progress-monitor (simple-monitor monitor-channel) uri local-dir)
    (catch Exception e
      (>!! monitor-channel (str "Error: " (.getMessage e)))))
  (close! monitor-channel))

(defn start-cloning [uri local-dir monitor-channel]
  (thread (clone-with-simple-monitor uri local-dir monitor-channel)))

;(def monitor (chan))

;(thread
; (loop []
;   (when-let [msg (<!! monitor)]
;     (print msg)
;     (recur)))
; (println "Done"))

;(start-cloning "git@github.com:klauswuestefeld/simploy.git" "tmp/simploy" monitor)

;(close! monitor)

(def responses-by-product-path (atom {}))

(defn github-uri [peer product]
  (format "git@github.com:%s/%s.git" peer product))

(defn assoc-if-absent! [map-in-atom key new-value]
  (loop []
    (swap! map-in-atom #(if (get % key) % (assoc % key new-value)))
    (if-let [value (get @map-in-atom key)]
      value
      (recur)))) ;Might have been cleared by other thread.

(defn serve-clone-request [peer product local-dir response-channel]
  (let [my-mult (async/mult (async/chan 100))
        mult (assoc-if-absent! responses-by-product-path local-dir my-mult)]
    (async/tap mult response-channel)
    (if (identical? mult my-mult)
      (start-cloning (github-uri peer product)
                     local-dir
                     response-channel))))

(def c (async/chan 100))
(def m (async/mult c))
(>!! c 34)
