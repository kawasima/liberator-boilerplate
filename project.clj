(defproject net.unit8/liberator-boilerplate "0.1.0-SNAPSHOT"
  :description "Boilerplate for API server using liberator."
  :url "https://github.com/kawasima/liberator-boilerplate.git"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ [org.clojure/clojure "1.5.1"]
                  [liberator "0.10.0"]
                  [compojure "1.1.5"]
                  [ring/ring-devel "1.2.0"]
                  [korma "0.3.0-RC6"]
                  [com.h2database/h2 "1.3.170"]
                  [http-kit  "2.1.13"]
                  [drift "1.5.2"]]
  :main example.liberator.core
  :resource-paths ["lib/*", "resources"]
  :ring {:handler example.liberator.core/app}
  :plugins [ [lein-ring "0.8.2"]
             [drift "1.5.2"]])
