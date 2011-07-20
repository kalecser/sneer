(ns randest.main
  (:require clojure.contrib.io)
  (:import
    (sneer.tests.adapters SneerCommunity SneerParty)
    (sneer.foundation.testsupport CleanTestBase)
    (sneer.foundation.brickness Brickness)
    (sneer.foundation.environments Environments)
    (sneer.foundation.lang ClosureX)
    (sneer.bricks.hardware.io.log Logger)
    (sneer.bricks.hardware.io.log.tests LoggerMocks)
    (java.io File)))

(defmacro my
  ([brick]
    `(Environments/my ~brick))
  ([brick method & args]
    `(. (Environments/my ~brick) ~method ~@args)))

(defn hours [n]  (* 1000 60 60 n))

(defn days [n]  (* 24 (hours n)))

(defn random-int [] (rand-int Integer/MAX_VALUE))

(defn rand-contact-nickname [party]
  (when-let [nicknames (seq (.contactNicknames party))]
    (rand-nth nicknames)))

(defn rand-string
  ([] (rand-string "p"))
  ([prefix] (str prefix (random-int))))

(defn change-amount-of-lent-space [party]
  (when-let [nick (rand-contact-nickname party)]
    (.lendSpaceTo party nick (rand-int (* 20 1024)))))

(defn add-important-file [party]
  (let [size (rand-int (* 10 1024))
        file-name (str "file" size "bytes")
        file (File. (.folderToSync party) file-name)]
    (spit file (byte-array size))))

(defn connect-parties [community]
  (when-let [parties (seq (.allParties community))]
    (let [a (rand-nth parties)
          b (rand-nth parties)]
	    (when (not= a b)
		    (my Logger log "connecting parties {} {}" (to-array (map #(.ownName %) [a b])))
		    (.connect community a b)))))

(defprotocol EventSource
  (event-types-for [this]))

(extend-protocol EventSource
  SneerCommunity
  (event-types-for [_] [
      [(hours 12) #(.createParty % (rand-string))]
      [(hours (Math/sqrt 12)) connect-parties]])
  
  SneerParty
  (event-types-for [_] [
      [(hours 4) #(.shout % (str "hi, " (random-int)))]
      [(days 1) change-amount-of-lent-space]
      [(hours 1) add-important-file]
      ]))

(defn new-environment []
  (let [loggerMocks (LoggerMocks.)
        logger (.newInstance loggerMocks)]
    (Brickness/newBrickContainer (to-array [loggerMocks logger]))))

(defn with-environment [e f]
  (Environments/runWith e (reify ClosureX (run [this] (f)))))

(defn test-my []
  (my Logger log "testing..." (to-array [])))

(defn print-logged-messages [e]
  (with-environment e #(my LoggerMocks printAllKeptMessages)))

(defrecord Event [timeout action source])

(defn next-event-for [current-time source]
  (let [types (event-types-for source)
        [period action] (rand-nth types)]
    (Event. (+ period current-time) #(action source) source)))

(defn randest-loop [root max-iterations]
  (loop [events [(next-event-for 0 root)]
         iteration 0]
     (let [current (first events)
           source (.source current)
           now (.timeout current)
           action (.action current)
           action-result (action)
           new-event-sources (if (nil? action-result) [] [action-result])
           next-event-for (partial next-event-for now)
           pending-events (concat (rest events) (map next-event-for new-event-sources) [(next-event-for source)])]
       (when (< iteration max-iterations)
         (print \.)
         (recur
           (sort-by first pending-events)
           (inc iteration))))))

(defn empty-folder [path]
  (let [folder (File. path)]
    (CleanTestBase/deleteFolder folder)
    folder))

(defn randest-main
  ([] (randest-main 20))
  ([max-iterations]
    (let [e (new-environment)
          folder (empty-folder "/tmp/root")
          root (SneerCommunity. folder)]
      (try 
        (do
          (with-environment e #(randest-loop root max-iterations))
          (print-logged-messages e))
        (finally (.crash root))))))
