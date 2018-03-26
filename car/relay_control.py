#!/usr/bin/env python    
import RPi.GPIO as GPIO  
import time  
import signal  
import atexit  
import sys
import config as CFG
  
GPIO.setmode(GPIO.BOARD)  
PIN=CFG.RELAY_PIN
GPIO.setup(PIN, GPIO.OUT)
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
