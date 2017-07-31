#!/usr/bin/env python 
import time
import sys
import controller

if __name__=="__main__":
    sleep=5
    if len(sys.argv)==2:
        sleep=float(sys.argv[1])
    try:
        controller.up(sleep)
    except KeyboardInterrupt:
        print "KeyboardInterrupt"
        controller.stop()
        

