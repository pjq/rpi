#!/usr/bin/env python 
import time
import controller

if __name__=="__main__":
    try:
        controller.t_down()
        time.sleep(5)
        controller.t_stop()
    except KeyboardInterrupt:
        print "KeyboardInterrupt"
        controller.t_stop()
        

