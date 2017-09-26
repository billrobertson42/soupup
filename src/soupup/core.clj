(ns soupup.core
  (:require [clojure.string :as str])
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Attribute Comment DataNode Document DocumentType
            Element Node TextNode]
           [org.jsoup.select Elements]))

(declare child-content)

(defn attr-seq [^Node node]
  "create a seq of attibutes on a node"
  (if (and (.attributes node) 
           (> (-> node .attributes .size) 0))
    (iterator-seq (-> node .attributes .iterator))))

(defn attr-pair [^Attribute a]
  [(keyword (.getKey a)) (.getValue a)])

(defn attr-map [^Node node]
  "Convert attributes to a map if present or nil if none.
   Map will be keyed by keywords, excluding id and class"
  (let [attrs (attr-seq node)]
    (if attrs 
      (into {} (filter #(not (#{:id :class} (first %))) 
                       (map attr-pair attrs))))))

(defn- conj-if [base element]
  "Conj element to base if it's not nil"
  (if (seq element)
    (conj base element)
    base))

(defn element-id [^Element e]
  (let [id (.attr e "id")]
    (if-not (str/blank? id) (str "#" id) "")))

(defn element-class [^Element e]
  (let [class-val (.attr e "class")]
    (if-not (str/blank? class-val)
      (str "." (str/join "." (str/split class-val #"\s+")) ""))))

(defn node-keyword [^Element e]
  (let [basename (.nodeName e)
        eid (element-id e)
        eclass (element-class e)]
    (keyword (str basename eid eclass))))

(defprotocol AsHiccup
  (as-hiccup [n preserve] "convert JSoup Structure to hiccup"))

(extend-protocol AsHiccup
  Comment
  (as-hiccup [^Comment c preserve]
    (str "<!--" (.getData c) "-->"))

  DataNode
  (as-hiccup [^DataNode dn preserve]
    (.getWholeData dn))

  Document
  (as-hiccup [^Document doc preserve]
    (child-content doc preserve))

  DocumentType
  (as-hiccup [^DocumentType dt preserve]
    (conj-if [:!doctype] (attr-map dt)))

  Element
  (as-hiccup [^Element e preserve] 
    (let [base (conj-if [(node-keyword e)] (attr-map e))
          children (child-content e preserve)]
      (cond 
        (or (string? children) (keyword? (first children)))
        (conj base children)

        (seq children)
        (into base children)

        :else
        base)))

  Elements
  (as-hiccup [^Elements el preserve]
    (if (> (.size el) 0) (map #(as-hiccup % preserve) el)))

  TextNode
  (as-hiccup [^TextNode tn preserve]
    (if preserve 
      (.getWholeText tn)
      (.text tn))))

(defn children-seq [^Node node]
  (seq (.childNodes node)))

(defn child-content [^Node node preserve]
  "Create child content if present, or nil if none."
  (if (= 1 (.childNodeSize node))
    (as-hiccup (.childNode node 0) preserve)
    (if-let [children (children-seq node)]
      (map #(as-hiccup % preserve) children))))

(defn soupup [^Document doc]
  "Convert a JSoup document to hiccup data structures."
  (as-hiccup doc false))

(defn soupup-preserve-ws [^Document doc]
  "Convert a JSoup document to hiccup data structures, preserve whitespace"
  (as-hiccup doc true))

(defn parse [^String html-text]
  "Shortcut for Jsoup/parse, returns a Jsoup document"
  (Jsoup/parse html-text))

(defn parsup [^String html-text]
  "parse and convert to a hiccup data structure"
  (soupup (parse html-text)))

(defn parsup-preserve-ws [^String html-text]
  "parse and convert to a hiccup data structure, preserve whitespace"
  (soupup-preserve-ws (parse html-text)))

(defn select [^Element element ^String css-selector]
  "Shortcut for (.select node css-selector), returns a Jsoup ElementList"
  (.select element css-selector))

(defn selectup [^Element element ^String css-selector]
  "Call select and convert, returns a lazy seq"
  (soupup (select element css-selector)))

(defn selectup-preserve-ws [^Element element ^String css-selector]
  "Call select and convert, returns a lazy seq, preserve whitespace"
  (soupup-preserve-ws (select element css-selector)))

(defn frag [^String html-frag-text]
  "Shortcut for parsing an html fragment, returns a Jsoup Element"
  (let [^Document doc (Jsoup/parseBodyFragment html-frag-text)]
    (-> doc .body (.childNode 0))))

(defn fragup [^String html-frag-text]
  "Shortcut for parsing an html fragment, returns a Hiccup data structure"
  (soupup (frag html-frag-text)))

(defn fragup-preserve-ws [^String html-frag-text]
  "Shortcut for parsing an html fragment, returns a Hiccup data structure, preserve whitespace"
  (soupup-preserve-ws (frag html-frag-text)))
