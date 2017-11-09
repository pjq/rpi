#!/usr/bin/python
import RPi.GPIO as GPIO
import time

SERVO = 5 
GPIO.setmode(GPIO.BCM)
GPIO.setup(SERVO, GPIO.OUT)
p = GPIO.PWM(SERVO, 50)
p.start(5)
p.ChangeDutyCycle(5)
time.sleep(1)
p.ChangeDutyCycle(7.5)
time.sleep(1)
p.ChangeDutyCycle(10)
time.sleep(1)
p.ChangeDutyCycle(7.5)
time.sleep(1)
p.ChangeDutyCycle(5)
time.sleep(1)
print "Done loop"
p.stop()
GPIO.cleanup()
