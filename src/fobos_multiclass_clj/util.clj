(ns fobos-multiclass-clj.util)

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
