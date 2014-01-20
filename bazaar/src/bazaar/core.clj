;(ns bazaar.core
;  (:use [clj-jgit.porcelain] [clojure.contrib.java.utils]))
;  (:use [clojure.contrib.java-utils]))

(ns bazaar.core
  (:require clojure.contrib.java-utils))

(def repo-name "trash")

(def repo-url (str "git@github.com:klauswuestefeld/" repo-name ".git"))

(delete-file-recursively repo-name)

(with-identity {repo-name {}}
  (def my-clone
    (git-clone-full repo-url repo-name)))

(def pull-result (.call (.pull (:repo my-clone))))
(.isSuccessful pull-result)

(def my-repo (:repo my-clone))

(git-status my-repo)
;=> {:untracked #{}, :removed #{}, :modified #{}, :missing #{}, :changed #{}, :added #{}}

;; List existing branches
(git-branch-list my-repo)
;=> (#<LooseUnpeeled Ref[refs/heads/master=526f58f0b09621ce27fbae575991c8311a515430]>)

;; Create a new local branch to store our changes
(git-branch-create my-repo "my-branch")
;=> #<LooseUnpeeled Ref[refs/heads/my-branch=526f58f0b09621ce27fbae575991c8311a515430]>

;; Prove to ourselves that it was created
(git-branch-list my-repo)
;=> (#<LooseUnpeeled Ref[refs/heads/master=526f58f0b09621ce27fbae575991c8311a515430]> #<LooseUnpeeled Ref[refs/heads/my-branch=526f58f0b09621ce27fbae575991c8311a515430]>)

;; Check out our new branch

(git-checkout my-repo "my-branch")
;=> #<LooseUnpeeled Ref[refs/heads/my-branch=526f58f0b09621ce27fbae575991c8311a515430]>

;; Now go off and make your changes.
;; For example, let's say we added a file "foo.txt" at the base of the project.
(git-status my-repo)
;=> {:untracked #{"foo.txt"}, :removed #{}, :modified #{}, :missing #{}, :changed #{}, :added #{}}

;; Add the file to the index
(git-add my-repo "whatever.txt")
;=> #<DirCache org.eclipse.jgit.dircache.DirCache@81db25>

;; Check for status change
(git-status my-repo)
;=> {:untracked #{}, :removed #{}, :modified #{}, :missing #{}, :changed #{}, :added #{"foo.txt"}}

;; Now commit your changes, specifying author and committer if desired
(git-commit my-repo "Add a file" {:name "Fulano" :email "fulano@example.com"})
;=> #<RevCommit commit 5e116173db370bf400b3514a4b093ec3d98a2666 1310135270 -----p>


;; Status clean
(git-status my-repo)
;=> {:untracked #{}, :removed #{}, :modified #{}, :missing #{}, :changed #{}, :added #{}}

(git-clean my-repo :clean-dirs? true, :ignore? true)

(first (git-log my-repo))

(with-identity {"trash" {}}
  (-> my-repo .push .call))