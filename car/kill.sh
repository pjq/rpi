#!/bin/sh

#echo kill `ps aux|grep auto_car.py|awk -F "  " '{print $3}'|grep -v "^$"`
list=`ps aux|grep auto_car.py|grep -v "grep"|awk -F "  " '{print $3}'`
echo ${list}
kill ${list}
list=`ps aux|grep auto_car.py|grep -v "grep"|awk -F "  " '{print $5}'`
echo ${list}
kill ${list}
list=`ps aux|grep auto_car.py|grep -v "grep"|awk -F " " '{print $2}'`
echo ${list}
kill ${list}
