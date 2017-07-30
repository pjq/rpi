#!/usr/bin/env python 
import time
import controller

if __name__=="__main__":
    try:
        controller.right(5)
    except KeyboardInterrupt:
        print "KeyboardInterrupt"
        controller.stop()
        

