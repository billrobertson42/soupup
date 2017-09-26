# soupup

A Clojure library designed to utilize [JSoup](http://jsoup.org) to
produce [Hiccup](https://github.com/weavejester/hiccup) data structures.

It also provides direct access to the underlying JSoup objects in
case you find those more convenient.

Finally it provides convenience functions for JSoup's parse (full 
document and fragment) and select methods. Both Hiccup and JSoup
object versions are provided.

## Include in your project

JSoup is hosted in Clojars, so if you're using Leiningen, just
add the dependency to your project.clj file.

    [soupup "0.2.0"]

## Usage

soupup.core contains several functions for use

    (parse)    
    Accepts html text and returns JSoup data structures.
    e.g. (parse (slurp "http://www.google.com"))

    (parsup)
    (parseup-preserve-whitespace)
    Accepts html text and returns Hiccup data structures.
    e.g. (parseup (slurp "http://www.google.com")) 

    (frag)
    Accepts html fragment text and returns JSoup data structures.
    e.g. (frag "<p>Hello World</p>")

    (fragup)
    (fragup-preserve-whitespace)
    Accepts html fragment text and returns Hiccup data structures.
    e.g. (fragup "<p>Hello World</p>")

    (select)   
    Accepts a JSoup data structure and a css selector and returns Jsoup
    data structures.
    e.g. (select (parse (slurp "http://www.google.com")) "img")

    (selectup)
    (selectup-preserve-whitespace)
    Accepts a JSoup data structure and a css selector and returns Hiccup
    data structures.
    e.g. (selectup (parse (slurp "http://www.google.com")) "img")

    (soupup)
    (soupup-preserve-whitespace)
    Convert JSoup data structures to Hiccup data structures.

The `-preserve-whitespace` versions of the functions will preserve the 
original whitespace, otherwise it will be normalized by JSoup.

At the time of this writing, 

    (selectup (parse (slurp "http://www.google.com")) "img")

returned the following...

    ([:img#hplogo
      {:alt "Gloria E. Anzalda’s 75th Birthday",
       :border "0",
       :height "200",
       :src "/logos/doodles/2017/gloria-e-anzalduas-75th-birthday-6115361035386880-l.png",
       :title "Gloria E. Anzalda’s 75th Birthday",
       :width "500",
       :onload "window.lol&&lol()"}])

Running this back through Hiccup's html function yields the following.

    <img alt="Gloria E. Anzalda’s 75th Birthday" border="0" height="200" 
         id="hplogo" onload="window.lol&amp;&amp;lol()" 
         src="/logos/doodles/2017/gloria-e-anzalduas-75th-birthday-6115361035386880-l.png" 
         title="Gloria E. Anzalda’s 75th Birthday" width="500" />

See http://jsoup.org/cookbook/extracting-data/selector-syntax information on Jsoup's css selectors.

## Running the Tests

    user=> (use 'soupup.test)
    user=> (test-all)

## License

Copyright © 2014-2017 Bill Robertson

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
