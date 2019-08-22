(ns clojure-shop.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [clojure-shop.core-test]))

(doo-tests 'clojure-shop.core-test)

