# fobos_multiclass_clj

Multiclass classifier based on FOBOS.

## Usage

FIXME

```sh
$ wget http://www.csie.ntu.edu.tw/\~cjlin/libsvmtools/datasets/multiclass/news20.bz2
$ wget http://www.csie.ntu.edu.tw/\~cjlin/libsvmtools/datasets/multiclass/news20.t.bz2

$ bzip2 -d news20.bz2
$ bzip2 -d news20.t.bz2

$ cat news20 news20.t > all.txt
$ lein run
Label: F-value
1: 0.71313672922252
2: 0.7157894736842103
3: 0.6534090909090907
4: 0.7183462532299743
5: 0.8320000000000001
6: 0.8671679197994986
7: 0.7639257294429709
8: 0.8235294117647058
9: 0.879177377892031
10: 0.7359999999999999
11: 0.7917737789203084
12: 0.8380952380952383
13: 0.869109947643979
14: 0.7848101265822787
15: 0.6018957345971566
16: 0.7353760445682449
17: 0.8418491484184916
18: 0.5000000000000001
19: 0.697560975609756
20: 0.7718446601941746
Accuracy: 0.7571283095723014
F-value: 0.7567398820287315
```

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
