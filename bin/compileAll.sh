#!/bin/bash

cp="."
for i in ../lib/*.jar 
do
	cp=$cp:$i
done

echo "Using Classpath $cp..."

javac -classpath $cp -d . -s ../src/ $(find ../src -name *.java | sed -e "s/\/[^/]*\.java$//g" | sort | uniq | sed -e "s/$/\/*.java/g")