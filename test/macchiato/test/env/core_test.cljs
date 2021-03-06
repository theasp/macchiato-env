(ns macchiato.test.env.core-test
  (:require
    [macchiato.env :as env]
    [cljs.test :refer-macros [is are deftest testing use-fixtures]]))

(deftest parse-config
  (is
    (= ["foo" "bar" 100 100.5 true true false false false true {:foo :bar}]
       (map env/str->value
            ["foo" "bar" "100" "100.5" "true" "True" "false" "False" "FALSE" "TRUE" "{:foo :bar}"]))))

(deftest test-path
  (is
    (=
      {:foo "string value",
       :foo-bar 1234,
       :bar-baz true,
       :bar {:baz false},
       :fooz-bar {:baz {:foo :bar}}}
      (reduce
        (fn [props [k v]]
          (assoc-in props (env/env->path k) (env/str->value v)))
        {}
        {"foo"           "string value"
         "foo-bar"       "1234"
         "bar_baz"       "true"
         "bar__baz"      "false"
         "fooz_bar__baz" "{:foo :bar}"}))))

(deftest merge-config
  (is
    (=
      {:foo "string value"
       :bar "new value"
       :new-key "I'm new"
       :fooz {:baz {:foo :bar :replace-me :replaced :merged-key :merged-value}}}
      (env/deep-merge
        {:foo "string value"
         :bar 1234
         :fooz {:baz {:foo :bar :replace-me :i-should-be-gone}}}
        {:bar "new value"
         :new-key "I'm new"
         :fooz {:baz {:merged-key :merged-value :replace-me :replaced}}}))))
