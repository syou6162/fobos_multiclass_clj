(ns fobos-multiclass-clj.core
  (:use [clojure.string :only (split)])
  (:use [fobos_clj.fobos])
  (:use [fobos_clj.svm]))

(defn binarized-label-examples [group-set label]
  (let [plus-one-examples (->> (get group-set label)
                               (map #(assoc-in % [0] 1)) ; 0 means class label
                               (vec))
        minus-one-examples (->> (dissoc group-set label)
                                (map second)
                                (apply concat)
                                (map #(assoc-in % [0] -1))
                                (vec))]
    (vec (shuffle (apply concat plus-one-examples [minus-one-examples])))))

(defn multiclass-examples [examples]
  (let [labels (map first examples)
        label-set (set labels)]
    (reduce (fn [result label]
              (assoc result
                label
                (binarized-label-examples
                 (group-by first examples)
                 label)))
            {}
            label-set)))

(defn argmax-label [models test-example]
  (->> models
       (reduce
        (fn [result [label svm]]
          (assoc result label (dotproduct (:weight svm) test-example)))
        {})
       (sort-by second >)
       (first)
       (first)))

(defn get-accuracy [gold prediction]
  (assert (= (count gold) (count prediction)))
  (let [sum (count (get (group-by (fn [[n1 n2]] (= n1 n2))
                                  (map vector gold prediction))
                        true))]
    (* 1.0 (/ sum (count gold)))))

(defn get-f-value [orig-gold orig-prediction label]
  (assert (= (count orig-gold) (count orig-prediction)))
  (let [binarize (fn [label xs] (vec (map #(if (= label %) 1 0) xs)))
        gold (binarize label orig-gold)
        prediction (binarize label orig-prediction)
        freq (frequencies (map vector gold prediction))
	tp (get freq [1 1] 0)
	tn (get freq [0 0] 0)
	fp (get freq [0 1] 0)
	fn (get freq [1 0] 0)
	recall (/ tp (+ tp fn))
	precision (/ tp (+ tp fp))]
    (/ (* 2.0 recall precision)
       (+ recall precision))))

(defn flip [f x y] (f y x))

(defn get-train-and-test-data [filename]
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
       (split-at 16000)))

(defn train-model [examples max-iter eta lambda]
  (let [init-model (update-weight
                    (fobos_clj.svm.SVM. examples eta lambda) 0)]
    (loop [iter 1
           model init-model]
      (if (= iter max-iter)
        model
        (recur (inc iter) (update-weight model iter))))))

(defn get-models [training-examples max-iter eta lambda]
  (reduce (fn [result [class examples]]
            (assoc result
              class
              (train-model examples max-iter eta lambda)))
          {}
          training-examples))

(defn mean [xs]
  (let [xs (filter #(not (Double/isNaN %)) xs)
        sum (reduce + xs)]
    (/ sum (count xs))))

(defn -main [& args]
  (let [filename "/Users/yasuhisa/Desktop/fobos_multiclass_clj/all.txt"
        [temp-training-examples test-examples] (get-train-and-test-data filename)
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
