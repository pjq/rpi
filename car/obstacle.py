#!/usr/bin/env python
# -*- coding:utf-8 -*-
import time
import RPi.GPIO as GPIO
import datetime as dt
import json
 
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

def has_obstacles():
   obstacle1=has_obstacle(PIN1)
   obstacle2=has_obstacle(PIN2)
   obstacle3=has_obstacle(PIN3)
   obstacle4=has_obstacle(PIN4)
   value={'obstacle1':obstacle1, "obstacle2":obstacle2, "obstacle3":obstacle3, "obstacle4":obstacle4 }
   #print "obstacles: %s %s %s %s "%(obstacle1, obstacle2, obstacle3, obstacle4)
   #value=json.dumps(value, ensure_ascii=False)
   return value
    
if __name__=="__main__":
    while  1:
       time.sleep(0.2)
       value=has_obstacles()
       print value
       

