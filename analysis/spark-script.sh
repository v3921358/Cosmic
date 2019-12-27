#!/bin/bash

scala_file=$1

shift 1

arguments=$@

spark-shell -i <(echo 'val args = "'$arguments'".split("\\s+")' ; cat $scala_file)