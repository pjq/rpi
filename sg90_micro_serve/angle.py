#!/usr/bin/env python    
import RPi.GPIO as GPIO  
import time  
import signal  
import atexit  
import sys
  
#atexit.register(GPIO.cleanup)    
  
SERVO=5
GPIO.setmode(GPIO.BCM)  
GPIO.setup(SERVO, GPIO.OUT, initial=False)  
p = GPIO.PWM(SERVO,50) #50HZ  
p.start(0)  
time.sleep(1)  
DURATION=0.10
DURATION=0.05
DURATION=0.20
SLEEP=0.02

def test():
    while(True):  
      for i in range(0,181,5):  
        print i
        p.ChangeDutyCycle(2.5 + 10 * i / 180)
        time.sleep(SLEEP)                    
        p.ChangeDutyCycle(0)               
        time.sleep(DURATION)  

def set_angle(angle=0):
    dc=2.5 + 10 * angle / 180
    print dc,angle
    p.ChangeDutyCycle(dc)
    time.sleep(SLEEP)                    
    p.ChangeDutyCycle(0)               
    
if __name__=="__main__":
    if len(sys.argv)==2:
        angle=float(sys.argv[1])
        set_angle(angle)
