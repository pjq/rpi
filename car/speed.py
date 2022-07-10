#!/usr/bin/env python 
import time
import sys
import controller

if __name__ == "__main__":
    speed = 1
    if len(sys.argv) == 2:
        speed = float(sys.argv[1])
    try:
        controller.pwm_change_duty(speed)
    except KeyboardInterrupt:
        print("KeyboardInterrupt")
        controller.go_stop()
