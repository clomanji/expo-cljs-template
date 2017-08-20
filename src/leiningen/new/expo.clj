(ns leiningen.new.expo
  (:require [leiningen.new.templates :refer [renderer raw-resourcer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "expo"))
(def raw (raw-resourcer "expo"))

(defn templates-by-lib
  [lib data]
  (get
   {:om [["project.clj" (render "om/project.clj" data)]
         ["build.boot" (render "om/build.boot" data)]
         ["src/{{sanitized}}/core.cljs" (render "om/core.cljs" data)]
         ["src/{{sanitized}}/state.cljs" (render "om/state.cljs" data)]
         ["env/dev/env/main.cljs" (render "om/main_dev.cljs" data)]
         ["src/re_natal/support.cljs" (render "om/support.cljs" data)]]
    :reagent [["project.clj" (render "reagent/project.clj" data)]
              ["build.boot" (render "reagent/build.boot" data)]
              ["src/{{sanitized}}/core.cljs" (render "reagent/core.cljs" data)]
              ["src/{{sanitized}}/db.cljs" (render "reagent/db.cljs" data)]
              ["src/{{sanitized}}/handlers.cljs" (render "reagent/handlers.cljs" data)]
              ["src/{{sanitized}}/subs.cljs" (render "reagent/subs.cljs" data)]
              ["env/dev/env/main.cljs" (render "reagent/main_dev.cljs" data)]
              ["src/reagent/dom.cljs" (render "src/reagent/dom.cljs" data)]
              ["src/reagent/dom/server.cljs" (render "src/reagent/dom/server.cljs" data)]]
    :rum [["project.clj" (render "rum/project.clj" data)]
          ["build.boot" (render "rum/build.boot" data)]
          ["src/{{sanitized}}/core.cljs" (render "rum/core.cljs" data)]
          ["env/dev/env/main.cljs" (render "rum/main_dev.cljs" data)]
          ["src/re_natal/support.cljs" (render "rum/support.cljs" data)]
          ["src/sablono/core.cljs" (render "rum/sablono_core.cljs" data)]
          ["src/sablono/compiler.clj" (render "rum/sablono_compiler.clj" data)]]}
   lib))

(defn expo [name & lib]
  (main/info "Generating fresh Expo project.")
  (main/info "README.md contains instructions to get you started.")
  (main/info "If you have any questions, you can ask us on Expo slack #clojurescript channel. https://slack.expo.io/")

  (let [data {:name name
              :sanitized (name-to-path name)}
        lib (if-let [lib (first lib)]
              (let [v (keyword (clojure.string/replace lib "+" ""))]
                v)
              :reagent)]
    (->>
     [[".babelrc" (render ".babelrc" data)]
      [".gitignore" (render ".gitignore" data)]
      [".projectile" (render ".projectile" data)]
      ["LICENSE" (render "LICENSE" data)]
      ["app.json" (render "app.json" data)]
      ["js/figwheel-bridge.js" (render "js/figwheel-bridge.js" data)]
      ["assets/icons/app.png" (raw "assets/icons/app.png")]
      ["assets/icons/loading.png" (raw "assets/icons/loading.png")]
      ["assets/images/cljs.png" (raw "assets/images/cljs.png")]
      ["assets/images/cljs@2x.png" (raw "assets/images/cljs@2x.png")]
      ["assets/images/cljs@3x.png" (raw "assets/images/cljs@3x.png")]
      ["package.json" (render "package.json" data)]
      ["readme.md" (render "readme.md" data)]
      ["env/tools/user.clj" (render "env/tools/user.clj" data)]
      ["env/tools/externs.clj" (render "env/tools/externs.clj" data)]
      ["env/prod/env/main.cljs" (render "env/prod/env/main.cljs" data)]
      ["src/cljsjs/create_react_class.cljs" (render "src/cljsjs/create_react_class.cljs" data)]
      ["src/cljsjs/react.cljs" (render "src/cljsjs/react.cljs" data)]
      ["src/cljsjs/react/dom.cljs" (render "src/cljsjs/react/dom.cljs" data)]
      ["src/cljsjs/react/dom/server.cljs" (render "src/cljsjs/react/dom/server.cljs" data)]]
     (concat (templates-by-lib lib data))
     (apply ->files data))))
