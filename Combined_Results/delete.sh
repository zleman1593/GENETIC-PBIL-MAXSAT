#!/bin/bash

for file in *.txt ; do
    sed '/^$/d' $file > tt 
    mv tt $file
done