#!/usr/bin/env python    
  
  
import RPi.GPIO as GPIO  
import time  
import signal  
import atexit  
  
#atexit.register(GPIO.cleanup)    
  
GPIO.setmode(GPIO.BCM)  
GPIO.setup(14, GPIO.OUT, initial=False)  
p = GPIO.PWM(17,50) #50HZ  
p.start(0)  
time.sleep(2)  
  
while(True):  
  for i in range(0,181,10):  
    p.ChangeDutyCycle(2.5 + 10 * i / 180)
    time.sleep(0.02)                    
    p.ChangeDutyCycle(0)               
    time.sleep(0.2)  
    
  for i in range(181,0,-10):  
    p.ChangeDutyCycle(2.5 + 10 * i / 180)  
    time.sleep(0.02)  
    p.ChangeDutyCycle(0)  
    time.sleep(0.2)  
