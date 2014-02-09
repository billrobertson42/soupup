(ns soupup.core
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Attribute Comment DataNode Document Element Node 
            TextNode]
           [org.jsoup.select Elements]))

(declare child-content)

(defn attr-seq [^Node node]
  (if (and (.attributes node) 
           (> (-> node .attributes .size) 0))
    (iterator-seq (-> node .attributes .iterator))))
    
(defn attr-map [^Node node]
  "Convert attributes to a map if present or nil if none.
   Map will be keyed by keywords"
  (let [attrs (attr-seq node)]
    (if attrs (into {} (map #(vector (keyword (.getKey %)) (.getValue %)) attrs)))))

(defn- conj-if [base element]
  "Conj element to base if it's not nil"
  (if element
    (conj base element)
    base))

(defprotocol AsHiccup
  (soupup [n] "convert JSoup Structure to hiccup"))

(extend-protocol AsHiccup
  Element
  (soupup [^Element e] 
    (let [base [(keyword (.nodeName e))]]
      (-> base (conj-if (attr-map e)) (conj-if (child-content e)))))          

  TextNode
  (soupup [^TextNode tn]
    (.text tn))

  Elements
  (soupup [^Elements el]
    (if (> (.size el) 0) (map soupup el)))

  Document
  (soupup [^Document doc]
    (soupup (.childNode doc 0)))

  DataNode
  (soupup [^DataNode dn]
    (.getWholeData dn))

  Comment
  (soupup [^Comment c]
    (str "<!--" (.getData c) "-->"))
)

(defn children-seq [^Node node]
  (seq (.childNodes node)))

(defn child-content [^Node node]
  "Create a lazy seq of child content if present, or nil if none."
  (if (= 1 (-> node .childNodes .size))
    (soupup (.childNode node 0))
    (if-let [children (children-seq node)]
      (map soupup children))))

(defn parse [^String html-text]
  "Shortcut for Jsoup/parse, returns a Jsoup document"
  (Jsoup/parse html-text))

(defn parsup [^String html-text]
  "parse and convert to a hiccup data structure"
  (soupup (parse html-text)))

(defn select [^Node node ^String css-selector]
  "Shortcut for (.select node css-selector), returns a Jsoup ElementList"
  (.select node css-selector))

(defn selectup [^Node node ^String css-selector]
  "Call select and convert, returns a lazy seq"
  (soupup (select node css-selector)))

(defn frag [^String html-frag-text]
  "Shortcut for parsing an html fragment, returns a Jsoup Element"
  (let [^Document doc (Jsoup/parseBodyFragment html-frag-text)]
    (-> doc .body (.childNode 0))))

(defn fragup [^String html-frag-text]
  "Shortcut for parsing an html fragment, returns a Hiccup data structure"
  (soupup (frag html-frag-text)))
