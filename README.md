# REPLME
[REPLME](http://replme.clojurecup.com) is a project aimed at making
it easy to try out Clojure software by providing a containerized 
environment to download libraries and run them. 

## TryIt

If you're running this on REPLME. Here are some examples to click on
and try;

```clojure
(reduce * 10 [2 3 4 50])
```

```clojure
(doseq [n (range 0 50)]
  (println n))
```

```clojure
(wontwork!)
```

```clojure
(defn afn
  [an-arg-fn]
  (->> (map an-argn-fn [1 2 3 4 3 2 9 3])
       (map pr-str)
       (apply str)))

(afn #(vector (* 10 %) (* 100 %)))
```

```clojure
(require '[clojure.string :as str :refer [join]])

(join ["Vote" "for" "us" "in" "the" "Clojure" "Cup!"] " ")
```

## Usage

REPLME uses [Docker](http://docker.com) to run nREPL which it then
connects to over Websokets. To run REPLME you'll need a machine with
Docker installed. Since non-Linux computers cannot run Docker natively
we provide a Vagrant file, compatible with Vagrant > 1.5 and VirtualBox
> 4.3, configured to install Docker. To use

``` bash
vagrant up
vagrant ssh
cd /vagrant
lein run -m 8080 localhost:8181
```

You'll also need to run `lein cljsbuild once` either on vagrant or
your local machine to compile the front end.

Once you've done that

## License

Copyright 2014 Edward Paget and Alex Weiksnar
[APGLv3](https://www.gnu.org/licenses/agpl-3.0.html). See LICENSE
