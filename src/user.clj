(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [clojure.tools.nrepl.server :refer (start-server)]
            [edeposit.amqp.epubcheck.systems :refer [prod-system]]))

;;(defonce server (start-server :port 12345))
;;(reloaded.repl/set-init! prod-system)
