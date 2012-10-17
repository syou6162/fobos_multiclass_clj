(defproject fobos_multiclass_clj "0.1.1"
  :description "Multiclass classifier based on FOBOS"
  :url "https://github.com/syou6162/fobos_multiclass_clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [fobos_clj "0.0.3"]]
  :jvm-opts ["-Xmx3g" "-server" "-Dfile.encoding=UTF-8"]
  :main fobos-multiclass-clj.core)