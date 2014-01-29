(ns bazaar.git
  "custom git functions."
  (:require [clojure.java.io :as io]
            [clj-jgit.util :as util])
  (:import [org.eclipse.jgit.api Git]))

(defn clone-with-progress-monitor
  ([pm uri]
     (clone-with-progress-monitor pm uri (util/name-from-uri uri) "origin" "master" false))
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
