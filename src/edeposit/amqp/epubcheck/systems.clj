(ns edeposit.amqp.epubcheck.systems
  (:require [system.core :refer [defsystem]]
            [com.stuartsierra.component :as component]
            (system.components [rabbitmq :refer [new-rabbit-mq]]
                               [repl-server :refer [new-repl-server]]
                               )
            [environ.core :refer [env]]
            [edeposit.amqp.epubcheck.components :refer [new-epubcheck-amqp]]))

(defn prod-system []
  (component/system-map
   :rabbit-mq (new-rabbit-mq (env :rabbit-uri))
   ;:nrepl-server (new-repl-server (:nrepl-port options))
   :epubcheck-amqp (component/using
                    (new-epubcheck-amqp (env :epubcheck-amqp-exchange) 
                                        (env :epubcheck-amqp-qname))
                    [:rabbit-mq])
   )
)
