#!/usr/bin/env python3 
import time
import sys
import controller

if __name__ == "__main__":
    sleep = 1
    speed = 50
    if len(sys.argv) == 3:
        sleep = float(sys.argv[1])
        speed = float(sys.argv[2])
    try:
        controller.left(sleep, speed)
    except KeyboardInterrupt:
        print("KeyboardInterrupt")
        controller.go_stop()
