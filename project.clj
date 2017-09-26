(defproject soupup "0.2.0"
  :description "Thin Clojure wrapper for JSoup, translate to hiccup data structures or easy access to JSoup objects."
  :url "https://github.com/billrobertson42/soupup"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.jsoup/jsoup "1.10.3"]]
  :profiles {:dev {:dependencies [[hiccup "1.0.5"]]}}
)

