(ns edeposit.amqp.epubcheck.components
  (:require [com.stuartsierra.component :as component]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [langohr.exchange  :as lx]
            [edeposit.amqp.epubcheck.handlers :refer [handle-delivery]]
            )
  )

(comment ;; hook for emacs
  (add-hook 'after-save-hook 'restart-app nil t)
)

(defrecord EPubCheck-AMQP [rabbit-mq exchange qname consumer]
  component/Lifecycle
  (start [this]
    (println "starting EPubCheck AMQP client")
    (let  [ch (:ch rabbit-mq)
           handler (fn [ch metadata payload] 
                     (handle-delivery ch exchange metadata payload)
                     )
           ]
      (println "declaring topic exchange: " exchange)
      (lx/topic ch exchange {:durable true})
      (lq/declare ch qname {:durable true})
      (lq/bind ch qname exchange {:routing-key "request"})
      (let [consumer (lc/create-default ch {:handle-delivery-fn handler})]
        (lb/consume ch qname consumer {:auto-ack false})
        (assoc this :consumer consumer)))
    )
  
  (stop [this]
    (println "stopping EPubCheck AMQP client")
    this
    )
  )

(defn new-epubcheck-amqp [exchange qname]
  (map->EPubCheck-AMQP {:exchange exchange :qname  qname}))
