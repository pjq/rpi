#!/usr/bin/env python    
import RPi.GPIO as GPIO  
import time  
import signal  
import atexit  
import sys
  
#GPIO.setmode(GPIO.BCM)  
#PIN=24
#GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)  
PIN=24
#GPIO.setup(PIN, GPIO.OUT, initial=False)
GPIO.setup(PIN, GPIO.OUT)
#GPIO.setup(PIN, GPIO.IN)
time.sleep(0.2)  

def relay(on):
    print "turn on", on
    GPIO.output(PIN, on)

def relay_status():
    return GPIO.input(PIN) == True

if __name__=="__main__":
    if len(sys.argv)==2:
            on=sys.argv[1]
            if on == 'on':
                relay(GPIO.HIGH)
            elif on == 'off':
                relay(GPIO.LOW)
