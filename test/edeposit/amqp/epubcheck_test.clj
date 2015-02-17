(ns edeposit.amqp.epubcheck-test
  (:require [clojure.java.io :as io]
            [clojure.data.xml :as x]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as xml]
            [edeposit.amqp.epubcheck.core :as core]
            [edeposit.amqp.epubcheck.handlers :as handlers]
            [clojure.string :as s]
            )
  (:use clojure.test))

(deftest epub-file-test-01
  (let [fname "resources/vPrompt-Sample-EPUB2.epub"
        result (core/validate fname)
        xmldata (-> result :xml x/parse-str zip/xml-zip)
        ]
    (testing "check valid epub2 file and its metadata"
      (is (= (:isWellFormedEPUB2 result) true))
      (is (= (:isWellFormedEPUB3 result) false))
      (is (.startsWith (:xml result) "<?xml version"))
      (is (.contains (:xml result) "<jhove"))
      (is (= (:validationMessages result) ()))
      )
    (testing "check xml with jhove"
      (is (= (xml/xml1-> xmldata (xml/attr :name)) "epubcheck"))
      (is (= (xml/xml1-> xmldata :repInfo :format xml/text) "application/epub+zip"))
      (is (= (xml/xml1-> xmldata :repInfo :mimeType xml/text) "application/epub+zip"))
      (is (= (xml/xml1-> xmldata :repInfo :status xml/text) "Well-formed"))
      )
    )
  )

(deftest epub-file-test-02
  (let [fname "resources/vPrompt-Sample-EPUB3.epub"
        result (core/validate fname)
        ]
    (testing "check valid epub2 file and its metadata"
      (is (= (:isWellFormedEPUB2 result) false))
      (is (= (:isWellFormedEPUB3 result) true))
      (is (.startsWith (:xml result) "<?xml version"))
      (is (.contains (:xml result) "<jhove"))
      (is (not (=  (:validationMessages result) ())))
      )
    )
  )

(deftest epub-file-test-03
  (let [fname "resources/invalid/mimetypeAndVersion.epub"
        result (core/validate fname)
        messages (list "OPF-019, FATAL, [Spine tag was not found in the OPF file.], OEBPS/content.opf"
                       "PKG-006, ERROR, [Mimetype file entry is missing or is not the first file in the archive.], mimetypeAndVersion.epub"
                       "OPF-024, ERROR, [Found unknown ePub version 0.0.], mimetypeAndVersion.epub"
                       "OPF-001, ERROR, [There was an error when parsing the ePub version: Version attribute not found.], OEBPS/content.opf")
        ]
    (testing "check invalid epub2 file and validation messages"
      (is (= (:isWellFormedEPUB2 result) false))
      (is (= (:isWellFormedEPUB3 result) false))
      (is (.startsWith (:xml result) "<?xml version"))
      (is (.contains (:xml result) "<jhove"))
      (is (= (:validationMessages result) messages))
      )
    )
  )

(deftest epub-file-test-04
  (let [fname "resources/invalid/no-existing.bin"
        result (core/validate fname)
        ]
    (testing "check not existing file"
      (is (= (:isWellFormedEPUB2 result) false))
      (is (= (:isWellFormedEPUB3 result) false))
      (is (:xml result) "")
      (is (.contains (.toString (:validationMessages result)) 
                     "javax.xml.stream.XMLStreamException: ParseError"))
      )
    )
  )

(deftest epub-file-test-05
  (let [fname "resources/invalid/data.epub"
        result (core/validate fname)
        ]
    (testing "check valid epub3 file and validation messages"
      (is (= (:isWellFormedEPUB2 result) false))
      (is (= (:isWellFormedEPUB3 result) true))
      (is (:xml result) "")
      )
    )
  )

(deftest handlers-test-01
  (let [fname "resources/vPrompt-Sample-EPUB2.epub"
        file (io/file fname)
        metadata (read-string (slurp "resources/request-metadata.clj"))
        payload (.getBytes (slurp "resources/request-payload.bin"))
        result (handlers/parse-and-validate metadata payload)
        xmldata (-> result (:xml) x/parse-str zip/xml-zip)
        ]

    (testing "check valid epub3 file and validation messages"
      (is (= (:isWellFormedEPUB2 result) true))
      (is (= (:isWellFormedEPUB3 result) false))
      (is (empty? (:validationMessages result)))
      (is (= (xml/xml1-> xmldata (xml/attr :name)) "epubcheck"))
      (is (= (xml/xml1-> xmldata :repInfo :format xml/text) "application/epub+zip"))
      (is (= (xml/xml1-> xmldata :repInfo :mimeType xml/text) "application/epub+zip"))
      (is (= (xml/xml1-> xmldata :repInfo :status xml/text) "Well-formed"))
      )
    )
  )
