(defproject edeposit.amqp.epubcheck "0.1.0"
  :description "Wrapper aroung epubcheck. With AMQP and cli."
  :url "https://github.com/edeposit/edeposit.amqp.epubcheck"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [clj-time "0.8.0"]
                 [org.clojure/data.json "0.2.5"]
                 [org.idpf/epubcheck "4.0.0-alpha11"]
                 [com.novemberain/langohr "3.0.1"]
                 [environ "1.0.0"]
                 [org.clojure/tools.namespace "0.2.9"]
                 [me.raynes/fs "1.4.6"]
                 [reloaded.repl "0.1.0"]
                 [org.clojure/tools.nrepl "0.2.7"]
                 [commons-codec/commons-codec "1.10"]
                 [org.clojure/tools.logging "0.3.1"]
                 ]
  :main edeposit.amqp.epubcheck.main
  :aot [edeposit.amqp.epubcheck.main]
  :profiles {:dev {:plugins [
                             [quickie "0.3.6"]
                             [lein-ubersource "0.1.1"]
                             ]}
             :uberjar {:aot :all}
             }
  :plugins [[lein-sphinx "1.0.1"]]
  :deploy-repositories [["releases" :clojars]]
  :sphinx {:builder :html
           :source "docs"
           :output "docs/HTML"
           :config "."
           :rebuild true
           :tags [:html, :draft]
           :nitpicky true
           :warn-as-error true
           :setting-values {
                            :pygments_style "solarizedlight"
                            :html_theme_options.linkcolor "#B86644"
                            :html_theme_options.visitedlinkcolor "#B86644"
                            }
           :html-template-values {
                                  :author "Albert Camus"
                                  :mascot "Fighting Ferret"
                                  }
           }
  )
