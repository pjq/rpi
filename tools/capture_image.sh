#!/bin/bash

echo "$0 filename or use the current date as the filename."
base_path='images'

file=`date +%y%m%d%H%M%S`
date_path=`date +%y%m%d`
base_path="${base_path}/${date_path}"
mkdir -p ${base_path}
if [ ${#} = 1 ];then
   file=${1}
fi

echo "Capture photo ${file}.jpg"

mkdir -p ${base_path}
raspistill -o ${base_path}/${file}.jpg
