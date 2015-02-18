(ns edeposit.amqp.epubcheck.components
  (:require [com.stuartsierra.component :as component]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.core :as lcor]
            [langohr.basic     :as lb]
            [langohr.exchange  :as lx]
            [langohr.channel :as lch]
            [edeposit.amqp.epubcheck.handlers :refer [handle-delivery]]
            [clojure.tools.logging :as log]
            )
  )

(comment ;; hook for emacs
  (add-hook 'after-save-hook 'restart-app nil t)
)

(defrecord EPubCheck-AMQP [uri exchange qname channel consumer connection]
  component/Lifecycle
  (start [this]
    (log/info "starting EPubCheck AMQP client")
    (let [ handler (fn [ch metadata payload] 
                     (handle-delivery ch exchange metadata payload)
                     )
          conn (lcor/connect {:uri uri})
          ch (lch/open conn)  ]
      (log/info "declaring topic exchange: " exchange)
      (lx/topic ch exchange {:durable true})
      (lq/declare ch qname {:durable true :auto-delete false})
      (lq/bind ch qname exchange {:routing-key "request"})
      (let [consumer (lc/create-default ch {:handle-delivery-fn handler})]
        (lb/consume ch qname consumer {:auto-ack false})
        (assoc this :consumer consumer :channel ch :connection conn)))
    )
  
  (stop [this]
    (log/info "stopping EPubCheck AMQP client")
    (lcor/close channel)
    (lcor/close connection)
    this
    )
  )

(defn new-epubcheck-amqp [uri exchange qname]
  (map->EPubCheck-AMQP {:uri uri :exchange exchange :qname  qname}))
