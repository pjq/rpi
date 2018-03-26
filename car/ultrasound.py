#!/usr/bin/env python
# -*- coding:utf-8 -*-
import time
import RPi.GPIO as GPIO
import datetime as dt
import config as CFG
 
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

#send
PIN1=ULTRASOUND_PIN1
#echo
PIN2=ULTRASOUND_PIN2

GPIO.setup(PIN1, GPIO.OUT)
GPIO.setup(PIN2, GPIO.IN)
GPIO.output(PIN1, GPIO.LOW)
 
def get_distance():
   # send 10us, trigger
   GPIO.output(PIN1, GPIO.HIGH)
   time.sleep(0.000010)
   GPIO.output(PIN1, GPIO.LOW)
   while GPIO.input(PIN2) == False:
       pass
   #echo start    
   t1 = time.time()
   while GPIO.input(PIN2):
       pass
   #echo end 
   t2 = time.time()
   t3 = t2-t1
   # 空气中1个标准大气压在温度15度时速度为340m/s
   # 25度为346m/s
   # 所以按照一秒钟34000厘米计算
   # 根据硬件文档，该模块探测距离在2-400cm之间
   # 测试范围的时间间隔应该为0.000117到0.023529
   # 为了方便取值自行变化一点
   #if 0.0235 > t3 > 0.00015:
   distance=-1
   distance = t3*34000/2
   #if 0.0235 *2 > t3 > 0.00015:
   #    distance = t3*34000/2
   return distance
    
if __name__=="__main__":
    while  1:
       time.sleep(0.2)
       distance=get_distance()
       print 'Distance: %f cm' % distance

