#!/usr/bin/env python    
import RPi.GPIO as GPIO  
import time  
import signal  
import atexit  
import sys
import controller
import json
  
def detect_loop():
    count=0
    while True:
        detected=controller.motion_detect()
        time.sleep(1)
        if detected:
            count+=1
            print "detected", count
            ontime=0
            while controller.motion_detect():
                time.sleep(1)
                ontime +=1
                print "delay ",ontime 

if __name__=="__main__":
    #detected=detect()
    #print "Detected:" , detected
    detect_loop()
