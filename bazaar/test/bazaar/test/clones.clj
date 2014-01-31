(ns bazaar.test.clones
  (:use [clojure.test])
  (:require [clojure.core.async :as async]
            [bazaar.clones :refer :all]
            [bazaar.git :as git]))

(deftest exception-is-reported
  (with-redefs [git/clone-with-progress-monitor (fn [& args] (throw (Exception. "oops")))]
    (let [subject (start-cloning-process)
          response-channel (async/chan)]
      (try
        (serve-clone-request subject "peer" "product" response-channel)
        (let [[response _] (async/alts!! [response-channel (async/timeout 500)])]
          (is (= '(:error "oops")
                 response)))
        (finally
          (stop-cloning-process subject))))))
