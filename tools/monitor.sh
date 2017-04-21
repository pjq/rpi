#!/bin/bash
echo `date` "start monitor pm2.5..."

sleep_seconds=60
i=0
while true;
do
    echo `date` "loop times "${i}
    sh capture_image.sh
    sleep ${sleep_seconds} 
    sh autoforward.sh
    i=$((i+1))
done


