#!/bin/bash
echo `date` "start rpi car monitor..."

sleep_seconds=120
i=0
while true;
do
    echo `date` "loop times "${i}
    #sh capture_image.sh
    sleep ${sleep_seconds} 
    sh autoforward.sh&
    i=$((i+1))
done


