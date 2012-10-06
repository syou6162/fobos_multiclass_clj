(ns fobos-multiclass-clj.core
  (:use [clojure.string :only (split)])
  (:use [fobos-multiclass-clj.util])
  (:use [fobos-multiclass-clj.multiclass
         :only (multiclass-examples argmax-label get-models)]))

(defn flip [f x y] (f y x))

(defn get-train-and-test-data [filename n]
  (->> (slurp filename)
       (flip split #"\n")
       (map #(flip split #" " %))
       (map (fn [datum]
              (let [label (first datum)
                    fvs (vec (map
                              (fn [chunk]
                                (let [[k v] (map #(Integer/parseInt %)
                                                 (split chunk #":"))]
                                  [k v]))
                              (rest datum)))]
                [label fvs])))
       (split-at n)))

(defn mean [xs]
  (let [xs (filter #(not (Double/isNaN %)) xs)
        sum (reduce + xs)]
    (/ sum (count xs))))

(defn -main [& args]
  (let [filename "/Users/yasuhisa/Desktop/fobos_multiclass_clj/all.txt"
        [temp-training-examples test-examples] (get-train-and-test-data filename 16000)
        training-examples (multiclass-examples temp-training-examples)
        iter 10
        eta 1.0
        lambda 1.0
        models (get-models training-examples iter eta lambda)

        gold (map first test-examples)
        prediction (map #(argmax-label models (second %)) test-examples)
        labels (set (map first training-examples))]
    (println "Label: F-value")
    (dorun (map #(println
                  (str % ": " (get-f-value gold prediction %)))
                (sort-by #(Integer/parseInt %) (reverse (map str labels)))))
    (println (str "Accuracy: " (get-accuracy gold prediction)))
    (println (str "F-value: " (mean (map #(get-f-value gold prediction %) labels))))))
