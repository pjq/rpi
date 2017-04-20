#!/bin/bash

echo "$0 filename or use the current date as the filename."

file=`date +%y%H%M%S`
if [ ${#} == 1 ];then
   file=${1}
fi

echo "Capture photo ${file}.jpg"

raspistill -o ${file}.jpg
