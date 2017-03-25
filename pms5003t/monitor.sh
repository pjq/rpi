#!/bin/bash
echo `date` "start monitor pm2.5..."

sleep_seconds=60
i=0
while true;
do
    echo `date` "loop times "${i}
    python weather_sensor.py
    sleep ${sleep_seconds} 
    i=$((i+1))
done


