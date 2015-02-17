(ns edeposit.amqp.epubcheck.main
  (:require [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.data.xml :as xml]
            [clojure.tools.cli :as cli]
            [clojure.reflect :as r]
            [clojure.pprint :as pp]
            [environ.core :refer [env]]
            [clojure.tools.nrepl.server :refer (start-server)]
            [edeposit.amqp.epubcheck.systems :refer [prod-system]]
            [reloaded.repl :refer [system init start stop go reset]]
            [edeposit.amqp.epubcheck.core :refer [validate]]
            )
  (:import  [com.adobe.epubcheck.tool EpubChecker]
            [java.io File]
            )
  (:gen-class :main true)
  )

(defn -main [& args]
  (let [ [options args banner] 
         (cli/cli args
                  [ "-f" "--file"]
                  [ "--amqp" :default false :flag true]
                  [ "-h" "--help" :default false :flag true]
                  )
         ]
    (when (:help options)
      (println banner)
      (System/exit 0)
      )
    (when (:amqp options)
      (defonce server (start-server :port 12345))
      (reloaded.repl/set-init! prod-system)
      (go)
      )
    (when (:file options)
      (println (xml/indent-str (validate (io/file (:file options)))))
      )
    )
  )
