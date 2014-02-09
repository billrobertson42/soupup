# soupup

A Clojure library designed to utilize [JSoup] (http://jsoup.org) to
produce [Hiccup] (https://github.com/weavejester/hiccup) data structures.

Also provides convenience functions for Jsoup's parse and select methods.

## Usage

soupup.core contains four functions for use

    (parse)    
    accepts html text and returns a JSoup data structures
    e.g. (parse (slurp "http://www.google.com"))

    (parseup)  
    accepts html text and returns a Hiccup data structures
    e.g. (parse (slurp "http://www.google.com")) 

    (frag)
    accepts html fragment text and returns JSoup data structures
    e.g. (frag "<p>Hello World</p>")

    (fragup)
    accepts html fragment text and returns Hiccup data structures

    (select)   
    Accepts a JSoup data structure and a [css selector] (http://jsoup.org/cookbook/extracting-data/selector-syntax) and returns a Jsoup
    data structure
    e.g. (select (parse (slurp "http://www.google.com")) "img")

    (selectup) 
    Accepts a JSoup data structure and a [css selector] (http://jsoup.org/cookbook/extracting-data/selector-syntax) and returns a Hiccup
    data structure
    e.g. (selectup (parse (slurp "http://www.google.com")) "img")

    (soupup)   
    Convert JSoup data structures to Hiccup

At the time of this writing, 

    (selectup (parse (slurp "http://www.google.com")) "img")

returned the following...

    ([:img
      {:alt "Google",
       :height "95",
       :src "/images/srpr/logo9w.png",
       :width "269",
       :id "hplogo",
       :onload "window.lol&&lol()",
       :style "padding:28px 0 14px"}])

Running this back through Hiccup's html function yields the following.

    <img alt="Google" height="95" id="hplogo" onload="window.lol&amp;&amp;lol()" src="/images/srpr/logo9w.png" style="padding:28px 0 14px" width="269" />

## Running the Tests

    user=> (use 'soupup.test)
    user=> (test-all)

## License

Copyright © 2014 Bill Robertson

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
