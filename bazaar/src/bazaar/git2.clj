(ns bazaar.git
  "custom git functions."
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async :refer [chan >!! <!! alts!! timeout thread]])
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
    (onUpdate [& _]
              (>!! monitor-channel "."))
    (onEndTask [& _]
               (>!! monitor-channel "\n"))
    (isCancelled []
                 false)))

(defn clone-with-simple-monitor [uri local-dir monitor-channel]
  (try
    (clone-with-progress-monitor (simple-monitor monitor-channel) uri local-dir)
    (catch Exception e
      (println (.getMessage e))
      (>!! monitor-channel [:error (.getMessage e)]))))

(defn start-cloning [uri local-dir monitor-channel]
  (thread (clone-with-simple-monitor uri local-dir monitor-channel)))


;(def monitor (chan 5000))
;(def thread2 (start-cloning "git@github.com:klauswuestefeld/byke.git" "tmp/byke" monitor))
;(alts!! [monitor (timeout 2000)])
