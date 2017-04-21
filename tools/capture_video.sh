#!/bin/bash

echo "$0 filename or use the current date as the filename."
base_path="images"

file=`date +%y%m%d%H%M%S`
if [ ${#} = 1 ];then
   file=${1}
fi

echo "Capture 10s video ${file}.h264"
raspivid -o ${base_path}/${file}.h264 -t 10000
MP4Box -fps 30 -add ${base_path}/${file}.h264 ${base_path}/${file}.mp4
rm ${base_path}/${file}.h264
