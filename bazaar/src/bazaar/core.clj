(ns bazaar.core
  (:use [clj-jgit.porcelain]))

(def my-repo
  (git-clone-full "https://github.com/semperos/clj-jgit.git" "local-folder/clj-jgit"))