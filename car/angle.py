#!/usr/bin/env python3    
import RPi.GPIO as GPIO
import time
import signal
import atexit
import sys
import config as CFG

# atexit.register(GPIO.cleanup)

SERVO = CFG.SERVO_PIN
GPIO.setmode(GPIO.BCM)
GPIO.setup(SERVO, GPIO.OUT, initial=False)
p = GPIO.PWM(SERVO, 50)  # 50HZ
p.start(0)
time.sleep(0.02)
DURATION = 0.10
DURATION = 0.20
DURATION = 0.05
SLEEP = 0.02


def reset_angle(angle=0):
    for i in range(0, 181, 5):
        print(i)
        if i >= angle:
            break;
        p.ChangeDutyCycle(2.5 + 10 * i / 180)
        time.sleep(SLEEP)
        p.ChangeDutyCycle(0)
        time.sleep(DURATION)


def set_angle(angle=0):
    dc = 2.5 + 10 * angle / 180
    print(dc, angle)
    p.ChangeDutyCycle(dc)
    time.sleep(SLEEP)
    p.ChangeDutyCycle(0)


if __name__ == "__main__":
    if len(sys.argv) == 2:
        angle = float(sys.argv[1])
        set_angle(angle)
        # reset_angle(angle)
