#!/usr/bin/env python
# -*- coding:utf-8 -*-
import time
import RPi.GPIO as GPIO
import datetime as dt
 
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

#obstacle sensors pin
PIN1=32
PIN2=36
PIN3=38
PIN4=40
GPIO.setup(PIN1, GPIO.IN)
GPIO.setup(PIN2, GPIO.IN)
GPIO.setup(PIN3, GPIO.IN)
GPIO.setup(PIN4, GPIO.IN)
 
def has_obstacle(pin):
   return GPIO.input(pin) == False
    
if __name__=="__main__":
    while  1:
       time.sleep(0.2)
       obstacle1=has_obstacle(PIN1)
       obstacle2=has_obstacle(PIN2)
       obstacle3=has_obstacle(PIN3)
       obstacle4=has_obstacle(PIN4)
       print "obstacles: %s %s %s %s "%(obstacle1, obstacle2, obstacle3, obstacle4)
       

