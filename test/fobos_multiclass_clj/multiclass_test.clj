(ns fobos-multiclass-clj.multiclass-test
  (:use [clojure.test]
        [fobos-multiclass-clj.multiclass]))

(def training-example [["A" [["hoge" 1] ["fuga" 1]]]
         ["B" [["piyo" 1] ["nyan" 1]]]
         ["A" [["hoge" 1]]]])

(deftest binarized-label-examples-test
  (let [training-example
        [["A" [["hoge" 1] ["fuga" 1]]]
         ["B" [["piyo" 1] ["nyan" 1]]]
         ["A" [["hoge" 1]]]]]
    (are [x y] (= (sort x) (sort y)) ;; shuffleが入ってくるので
         (binarized-label-examples (group-by first training-example) "A")
         [[1 [["hoge" 1]]]
          [-1 [["piyo" 1] ["nyan" 1]]]
          [1 [["hoge" 1] ["fuga" 1]]]]

         (binarized-label-examples (group-by first training-example) "B")
         [[-1 [["hoge" 1] ["fuga" 1]]]
          [1 [["piyo" 1] ["nyan" 1]]]
          [-1 [["hoge" 1]]]]
         
         (binarized-label-examples (group-by first training-example) "C")
         [[-1 [["hoge" 1]]]
          [-1 [["piyo" 1] ["nyan" 1]]]
          [-1 [["hoge" 1] ["fuga" 1]]]])))

(deftest multiclass-examples-test
  (let [training-example
        [["A" [["hoge" 1] ["fuga" 1]]]
         ["B" [["piyo" 1] ["nyan" 1]]]
         ["A" [["hoge" 1]]]]
        value-sort-fn (fn [examples]
                        (reduce (fn [result class]
                                  (update-in
                                   result
                                   [class]
                                   (comp vec sort)))
                                examples
                                (set (map first examples))))]
    (are [x y] (= (value-sort-fn x) (value-sort-fn y))
         (multiclass-examples training-example)
         {"B" [[1 [["piyo" 1] ["nyan" 1]]]
               [-1 [["hoge" 1] ["fuga" 1]]]
               [-1 [["hoge" 1]]]],
          "A" [[1 [["hoge" 1] ["fuga" 1]]]
               [1 [["hoge" 1]]]
               [-1 [["piyo" 1] ["nyan" 1]]]]})))

(deftest get-models-test
  (let [training-example [["A" [[1 1] [1 1]]]
                          ["B" [[2 1] [3 1]]]
                          ["A" [[2 1]]]]
        max-iter 10
        eta 1.0
        lambda 1.0]
    (is (= #{"A" "B"}
           (set (map first (get-models
                            (multiclass-examples training-example)
                            max-iter eta lambda)))))))

(deftest get-label-scores-test
  (let [training-example [["A" [[1 1] [1 1]]]
                          ["B" [[2 1] [3 1]]]
                          ["A" [[2 1]]]]
        test-example [[1 1] [1 1]]
        max-iter 10
        eta 1.0
        lambda 1.0
        models (get-models
                (multiclass-examples training-example)
                max-iter eta lambda)]
    (is (= (keys (get-label-scores models test-example))
           '("A" "B")))))