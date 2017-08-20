(set-env!
 :source-paths   #{"src" "env/tools"}
 :dependencies '[[ajchemist/boot-figwheel "0.5.4-6" :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 [com.cemerick/piggieback "0.2.1" :scope "test"]
                 [figwheel-sidecar "0.5.10" :scope "test"]
                 [react-native-externs "0.1.0"]

                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]
                 [org.clojure/core.async "0.3.442"]
                 [reagent "0.7.0" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server cljsjs/create-react-class]]
                 [re-frame "0.9.3"]])

(require
 '[boot-figwheel]
 '[cljs.build.api :as b]
 '[user :as user]
 '[externs :as externs])

(refer 'boot-figwheel :rename '{cljs-repl fw-cljs-repl})

(require 'boot.repl)
(swap! boot.repl/*default-middleware*
       conj 'cemerick.piggieback/wrap-cljs-repl)

(deftask development []
  (task-options!
   figwheel
   {:build-ids  ["main"]
    :all-builds [{:id "main"
                  :source-paths ["src" "env/dev"]
                  :figwheel true
                  :compiler     {:output-to     "not-used.js"
                                 :main          "env.main"
                                 :optimizations :none
                                 :output-dir    "."}}]
    :figwheel-options {:open-file-command "emacsclient"
                       :validate-config false}})
  identity)

(deftask user-prepare! []
  (with-pre-wrap fileset
    (user/prepare)
    fileset))

(deftask dev
  "boot dev, then input (fw-cljs-repl)"
  []
  (comp
   (development)
   (user-prepare!)
   (figwheel)
   (repl)))


(deftask prod
  []
  (externs/-main)
  (set-env! :source-paths #(conj % "env/prod"))
  (println "Start to compile clojurescript ...")
  (let [start (System/nanoTime)]
    (b/build ["src" "env/prod"]
             {:output-to     "main.js"
              :main          "env.main"
              :output-dir    "target"
              :static-fns    true
              :externs       ["js/externs.js"]
              :parallel-build     true
              :optimize-constants true
              :optimizations :advanced
              :closure-defines {"goog.DEBUG" false}})
    (println "... done. Elapsed" (/ (- (System/nanoTime) start) 1e9) "seconds")))
