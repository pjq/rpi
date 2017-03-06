#!/bin/bash
echo "start monitor pm2.5..."
for i in {1..100000}
do
    echo "loop times "${i}
    sleep 1
    ./g5.py
done


