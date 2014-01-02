# partial-fn

A simple partial function implementation for Clojure. (I plan to write about why I think partial function, such as the one implemented here, is a useful idea.)

## Usage

```clojure
➜  partial-fn  lein repl
nREPL server started on port 64587
REPL-y 0.2.0
Clojure 1.5.1
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)

user=> (use '[clojure.core.match :only (match)]) (use 'partial-fn.core)
nil
nil
user=> (define-partial-fn foo [a b]
  #_=>   [3 1] :nice
  #_=>   :else :aww-shucks)
#'user/foo
user=> (foo 3 1)
:nice
user=> (foo 3 2)
:aww-shucks
user=> (in-domain? foo 3 1)
true
user=> (in-domain? foo 3 3)
true
user=> Bye for now!%
```

## License

Copyright © 2014 Rahul Goma Phulore.

Distributed under the Eclipse Public License, the same as Clojure.
