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
```

## License

Copyright © 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
