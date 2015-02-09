(ns edeposit.amqp.epubcheck.handlers
  (:require [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [langohr.exchange  :as lx]
            [edeposit.amqp.epubcheck.core :refer [validate]]
            [me.raynes.fs :as fs]
            [clojure.data.json :as json]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [clojure.data.codec.base64 :as base64]
            [byte-streams :as bs]
            )
  (:import [java.io File])
  )

(comment ;; hook for emacs
  (add-hook 'after-save-hook 'restart-app nil t)
)

;; (add-hook 'after-save-hook 
(defn validate-with-file [metadata ^bytes payload]
  (let [ msg (json/read-str (String. payload) :key-fn keyword) ]
    (let  [tmp-dir (fs/temp-dir "handle-delivery-validate-with-file-")
           b64data-file (io/file tmp-dir "data.base64")
           data-file (io/file tmp-dir "data.epub")
           validate-result (io/file tmp-dir "validate-result.json") ]
      (bs/transfer (:b64_data msg) b64data-file)
      (with-open [in (io/input-stream b64data-file)
                  out (io/output-stream data-file) ]
        (base64/decoding-transfer in out))
      (let [result (validate (.toString data-file))]
        (fs/delete-dir tmp-dir)
        (assoc result :filename (:filename msg))
        )
      )
    )
  )

(defn handle-delivery [ch exchange metadata ^bytes payload]
  (defn send-response [msg]
    (lb/publish ch exchange "response" msg 
                {:UUID (:UUID metadata)
                 :content-type "edeposit/epubcheck-response"
                 :content-encoding "application/json"
                 }
                )
    )
  (-> (validate-with-file metadata payload)
      (json/write-str)
      (send-response)
      )
  (lb/ack ch (:delivery-tag metadata))
  )
