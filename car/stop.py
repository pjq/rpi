#!/usr/bin/env python 
import time
import sys
import os
import controller

if __name__=="__main__":
    sleep=1
    if len(sys.argv)==2:
        sleep=float(sys.argv[1])
    try:
        cmd="sh /home/pi/rpi/car/kill.sh"
        print cmd
        os.system(cmd)
        controller.stop(sleep)
    except KeyboardInterrupt:
        print "KeyboardInterrupt"
        controller.go_stop()
        

