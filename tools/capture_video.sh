#!/bin/bash

echo "$0 filename or use the current date as the filename."

file=`date +%y%H%M%S`
if [ ${#} == 1 ];then
   file=${1}
fi

echo "Capture 10s video ${file}.h264"
raspivid -o ${file}.h264 -t 10000
