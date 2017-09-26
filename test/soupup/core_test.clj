(ns soupup.core-test
  (:require [clojure.test :refer :all]
            [soupup.core :refer :all])
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Node]))

(defn child [^Node node]
  "return the first child of a node"
  (if (> (.childNodeSize node) 0) (.childNode node 0)))

(defn single [^String html & [tag]]
  "parse a single element and strip out the body and extra stuff that
   jsoup adds"
  (-> (Jsoup/parse html) (.select (or tag "body")) first child))

(def <a1> "<a href='http://example.com'>link  text</a>")
(def <a2> "<a class='foo' href='http://example.com'>link text  </a>")
(def <p>  "<p>  Something</p>")
(def <div1> "<div><p>p1</p></div>")
(def <div2> "<div><p>p1</p><p>p2</p></div>")
(def <br> "<br>")
(def comment-node "<p><!-- a comment --></p>")
(def <script> "<script>js.alert('zomg')</script>")
(def <id> "<div id='xid'/>")
(def <class> "<div class='xclass'/>")
(def <multi-class> "<div class='xclass yclass'/>")
(def <id-class> "<div id='xid' class='xclass'/>")
(def <id-multi-class> "<div id='xid' class='xclass yclass'/>")

(deftest attrs-test
  (testing "attrs"
    (is (nil? (attr-seq (single <p>))))
    (is (seq? (attr-seq (single <a1>))))
    (is (= {:href "http://example.com"} (attr-map (single <a1>))))
    (is (= {:href "http://example.com"} (attr-map (single <a2>))))
))

(deftest text-test
  (testing "conversion of text nodes"
    (is (= " Something" (soupup (-> (single <p>) child))))    
    ))

(deftest comment-test
  (testing "conversion of comment nodes"
    (is (= "<!-- a comment -->" (soupup (-> (frag comment-node) child))))
    ))

(deftest element-soupup-test
  (testing "element to soupup translation"
    (is (= [:br] (fragup <br>)))
    (is (= [:a {:href "http://example.com"} "link text"] (fragup <a1>)))
    (is (= [:a.foo {:href "http://example.com"} "link text "] (fragup <a2>)))
    (is (= [:p " Something"] (fragup <p>)))
    (is (= [:div [:p "p1"]] (fragup <div1>)))
    (is (= [:div [:p "p1"] [:p "p2"]] (fragup <div2>)))
    (is (= [:div#xid] (fragup <id>)))
    (is (= [:div.xclass] (fragup <class>)))
    (is (= [:div.xclass.yclass] (fragup <multi-class>)))
    (is (= [:div#xid.xclass] (fragup <id-class>)))
    (is (= [:div#xid.xclass.yclass] (fragup <id-multi-class>)))
    ))

(deftest element-soupup-preserve-ws-test
  (testing "element to soupup-preserve-ws translation"
    (is (= [:a {:href "http://example.com"} "link  text"] 
           (fragup-preserve-ws <a1>)))
    (is (= [:a.foo {:href "http://example.com"} "link text  "] 
           (fragup-preserve-ws <a2>)))
    (is (= [:p "  Something"] (fragup-preserve-ws <p>)))
    ))

(deftest datanode-test
  (testing "<script> & style data are special in jsoup"
    (is (= "js.alert('zomg')" (soupup (single <script> "script"))))
    ))

(deftest parse-test
  (testing "parsing!"
    (is (= :html (first (soupup (Jsoup/parse <p>)))))
    (is (instance? org.jsoup.nodes.Document (parse <p>)))
    (is (= :html (first (parsup <p>))))
    ))

(deftest frag-test
  (testing "fragments!"
    (is (= :p (first (soupup (frag <p>)))))
    (is (= :p (first (fragup <p>))))
    ))
    
(deftest select-test
  (testing "selecting things n' stuff"
    (is (instance? org.jsoup.select.Elements (select (parse <p>) "p")))
    (is (= :p (-> (selectup (parse <p>) "p") first first)))
    ))

(deftest doctypes
  (let [page "<html><head></head><body><p>hi</p></body></html>"
        dt-page (str "<!doctype html>" page)]
    (testing "without doctype"
      (is (= (parsup page) 
             [:html [:head] [:body [:p "hi"]]])))
    (testing "with doctype"
      (is (= (parsup dt-page)
             [[:!doctype {:name "html", :publicId "", :systemId ""}]
              [:html [:head] [:body [:p "hi"]]]])))))


