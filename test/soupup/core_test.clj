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

(def <a1> "<a href='http://example.com'></a>")
(def <a2> "<a class='foo' href='http://example.com'></a>")
(def <p>  "<p>Something</p>")
(def <div1> "<div><p>p1</p></div")
(def <div2> "<div><p>p1</p><p>p2</p></div>")
(def <br> "<br>")
(def comment-node "<p><!-- a comment --></p>")
(def <script> "<script>js.alert('zomg')</script>")

(deftest attrs-test
  (testing "attrs"
    (is (nil? (attr-seq (single <p>))))
    (is (seq? (attr-seq (single <a1>))))
    (is (= {:href "http://example.com"}
           (attr-map (single <a1>))))
    (is (= {:href "http://example.com" :class "foo"}
           (attr-map (single <a2>))))
                                               
    ))

(deftest text-test
  (testing "conversion of text nodes"
    (is (= "Something" (soupup (-> (single <p>) child))))
    ))

(deftest comment-test
  (testing "conversion of comment nodes"
    (is (= "<!-- a comment -->" (soupup (-> (single comment-node) child))))
))

(deftest element-soupup-test
  (testing "element to soupup translation"
    (is (= [:br] (soupup (single <br>))))
    (is (= [:a {:href "http://example.com"}] (soupup (single <a1>))))
    (is (= [:p "Something"] (soupup (single <p>))))
    (is (= [:div [:p "p1"]] (soupup (single <div1>))))
    (is (= [:div (seq [[:p "p1"] [:p "p2"]])] (soupup (single <div2>))))
    (is (= "js.alert('zomg')" (soupup (single <script> "script"))))
    ))

