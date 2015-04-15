(ns edeposit.amqp.epubcheck.handlers
  (:require [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [langohr.exchange  :as lx]
            [edeposit.amqp.epubcheck.core :refer [validate]]
            [me.raynes.fs :as fs]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            )
  (:import [org.apache.commons.codec.binary Base64])
  )

(comment ;; hook for emacs
  (add-hook 'after-save-hook 'restart-app nil t)
)

(defn parse-and-validate [metadata ^bytes payload]
  (let [msg (json/read-str (String. payload) :key-fn keyword) 
        data-file (fs/temp-file "epubcheck-amqp-" ".epub")
        ]
    (with-open [out (io/output-stream data-file)]
      (.write out (Base64/decodeBase64 (:b64_data msg)))
      )
    (let [result (validate (.toString data-file))]
      (.delete data-file)
      result
      )
    )
  )

(defn response-properties [metadata]
  {:headers {"UUID" (-> metadata :headers (get "UUID"))}
   :content-type "edeposit/epubcheck-response"
   :content-encoding "application/json"
   :persistent true
   }
  )

(defn handle-delivery [ch exchange metadata ^bytes payload]
  (log/info "new message arrived")
  (defn send-response [msg]
    (lb/publish ch exchange "response" msg (response-properties metadata))
    )
  (-> (parse-and-validate metadata payload)
      (json/write-str)
      (send-response)
      )
  (lb/ack ch (:delivery-tag metadata))
  (log/info "message ack")
  )
