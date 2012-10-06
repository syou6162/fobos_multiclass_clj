(ns fobos-multiclass-clj.multiclass
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

(defn get-models [training-examples max-iter eta lambda]
  (let [train-model train-model [examples max-iter eta lambda]
        (let [init-model (update-weight
                          (fobos_clj.svm.SVM. examples eta lambda) 0)]
          (loop [iter 1
                 model init-model]
            (if (= iter max-iter)
              model
              (recur (inc iter) (update-weight model iter)))))]
    (reduce (fn [result [class examples]]
              (assoc result
                class
                (train-model examples max-iter eta lambda)))
            {}
            training-examples)))