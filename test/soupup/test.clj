(ns soupup.test
  (:use [clojure.test]))

(defn test-myns[ns]
  (require ns :reload-all)
  (let [result (dissoc (run-tests ns) :type)]    
    (if (and (= 0 (:error result)) (= 0 (:fail result)))
      [:pass ns result]
      [:fail ns result])))

(defn test-all[]
  (println "***************************")
  (println (str (java.util.Date.)))
  (let [results (into [] (map test-myns ['soupup.core-test]))]
    (doseq [result results] (println result))))

