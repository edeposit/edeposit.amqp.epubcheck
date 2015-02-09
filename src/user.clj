(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [edeposit.amqp.epubcheck.systems :refer [prod-system]]))

(reloaded.repl/set-init! prod-system)
