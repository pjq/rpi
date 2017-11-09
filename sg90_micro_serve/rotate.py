#!/usr/bin/env python    
import RPi.GPIO as GPIO  
import time  
import signal  
import atexit  
  
#atexit.register(GPIO.cleanup)    
  
SERVO=5
GPIO.setmode(GPIO.BCM)  
GPIO.setup(SERVO, GPIO.OUT, initial=False)  
p = GPIO.PWM(SERVO,50) #50HZ  
p.start(0)  
time.sleep(1)  
DURATION=0.15
SLEEP=0.02

#MAX=181
BEGIN=70
MAX=171

while(True):  
  for i in range(BEGIN,MAX,10):  
    p.ChangeDutyCycle(2.5 + 10 * i / 180)
    time.sleep(SLEEP)                    
    p.ChangeDutyCycle(0)               
    time.sleep(DURATION)  
    
  for i in range(MAX,BEGIN,-10):  
    p.ChangeDutyCycle(2.5 + 10 * i / 180)  
    time.sleep(SLEEP)  
    p.ChangeDutyCycle(0)  
    time.sleep(DURATION)  
