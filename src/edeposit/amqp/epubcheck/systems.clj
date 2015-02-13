(ns edeposit.amqp.epubcheck.systems
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [edeposit.amqp.epubcheck.components :refer [new-epubcheck-amqp]]))

(defn prod-system []
  (component/system-map
   :epubcheck-amqp (new-epubcheck-amqp
                    (env :epubcheck-amqp-uri)
                    (env :epubcheck-amqp-exchange) 
                    (env :epubcheck-amqp-qname)
                    )
   )
)
