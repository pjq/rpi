#!/usr/bin/env python
# -*- coding:utf-8 -*-
import time
import RPi.GPIO as GPIO
import datetime as dt
 
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

#send
PIN1=8
#echo
PIN2=10
GPIO.setup(PIN1, GPIO.OUT)
GPIO.setup(PIN2, GPIO.IN)
GPIO.output(PIN1, GPIO.LOW)
 
while  1:
   time.sleep(0.5)
   # IO 触发，给10us的高电平
   # 模块自动发送8个40khz的方波
   GPIO.output(PIN1, GPIO.HIGH)
   time.sleep(0.000010)
   GPIO.output(PIN1, GPIO.LOW)
   # 获取发射完毕时间
   # 未接收为False，循环检查开始接受点
   # 转为True为开始接受
   while GPIO.input(PIN2) == False:
       pass
   t1 = time.time()
   # 循环检查开始接收，转为False。则为接受完毕
   while GPIO.input(PIN2):
       pass
   # 获取接受完毕时间
   t2 = time.time()
   # 计算发送与接收时间差
   t3 = t2-t1
   # 空气中1个标准大气压在温度15度时速度为340m/s
   # 25度为346m/s
   # 所以按照一秒钟34000厘米计算
   # 根据硬件文档，该模块探测距离在2-400cm之间
   # 测试范围的时间间隔应该为0.000117到0.023529
   # 为了方便取值自行变化一点
   #if 0.0235 > t3 > 0.00015:
   if 0.0235 *2 > t3 > 0.00015:
       distance = t3*34000/2
       print 'Distance: %f cm' % distance
   else:
       print 'Null'
