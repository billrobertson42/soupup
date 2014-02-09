(ns soupup.core
  (:import [org.jsoup.nodes Attribute Comment DataNode Document Element Node 
            TextNode]))

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
