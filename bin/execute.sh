#!/bin/bash

cp="."
for i in ../lib/*.jar 
do
	cp=$cp:$i
done

echo "Using Classpath $cp..."

java -classpath $cp Main $@