#!/bin/bash
echo "start monitor pm2.5..."
for i in {1..100000}
do
    echo "loop times "${i}
    ./g5.py
    sleep 30
done


