(ns edeposit.amqp.epubcheck.core
  (:require [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.data.xml :as xml]
            [clojure.tools.cli :as cli]
            [clojure.reflect :as r]
            [clojure.pprint :as pp]
            )
  (:import  [com.adobe.epubcheck.tool EpubChecker]
            [java.io File]
            )
  (:gen-class :main true)
  )

(defn validate [test-file]
  (let [tmp-file (File/createTempFile "epubcheck-validation-output-" ".xml")
        tmp-file-name (.toString tmp-file)
        ]
    (doto  (new EpubChecker)
      (.run (into-array String [test-file "--quiet" "--out" tmp-file-name])) 
      )

    (def xmldata
      (-> tmp-file-name
          (io/input-stream)
          (xml/parse)
          (zip/xml-zip)
          )
      )
    (def result
      {:isWellFormedEPUB2 (and
                           (=  (zip-xml/xml1-> xmldata :repInfo :status zip-xml/text) "Well-formed")
                           (=  (zip-xml/xml1-> xmldata :repInfo :version zip-xml/text) "2.0"))
       :isWellFormedEPUB3 (and
                           (=  (zip-xml/xml1-> xmldata :repInfo :status zip-xml/text) "Well-formed")
                           (=  (zip-xml/xml1-> xmldata :repInfo :version zip-xml/text) "3.0"))
       :validationMessages (zip-xml/xml-> xmldata :repInfo :messages :message zip-xml/text)
       :xml (xml/emit-str xmldata)
       }
      )
    (.delete tmp-file)
    result
    )
  )

;; 

(defn -main [& args]
  (let [ [options args banner] (cli/cli args
                                        [ "-f" "--file" :default "resources/vPrompt-Sample-EPUB2.epub"]
                                        [ "--amqp" :default false :flag true]
                                        [ "-h" "--help" :default false :flag true]
                                        )
         ]
    (when (:help options)
      (println banner)
      (System/exit 0)
      )
    (println (xml/indent-str (validate (io/file (:file options)))))
    )
  )
