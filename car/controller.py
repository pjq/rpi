#!/user/bin/env python 
import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

PIN1=11
PIN2=12
PIN3=15
PIN4=16 

#print PIN1,PIN2,PIN3,PIN4

GPIO.setup(PIN1,GPIO.OUT)
GPIO.setup(PIN2,GPIO.OUT)
GPIO.setup(PIN3,GPIO.OUT)
GPIO.setup(PIN4,GPIO.OUT)
#GPIO.setup(7,GPIO.IN)

UNLIMITED_TIME=1000

def stop():
    print "stop"
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, False)

def down(sleep_time):
    print "backward seconds:", sleep_time
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)
    if sleep_time > 0:
        time.sleep(sleep_time)
        stop()
    else:
        time.sleep(UNLIMITED_TIME)
        stop()

def up(sleep_time):
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, True)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, True)
    if sleep_time > 0:
        print "forward seconds:", sleep_time
        time.sleep(sleep_time)
        stop()
    else:
        print "forward seconds:", UNLIMITED_TIME
        time.sleep(UNLIMITED_TIME)
        stop()

def left(sleep_time):
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, True)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)
    if sleep_time > 0:
        print "turn left seconds:", sleep_time
        time.sleep(sleep_time)
        stop()
    else:
        print "turn left seconds:",UNLIMITED_TIME 
        time.sleep(UNLIMITED_TIME)
        stop()

def right(sleep_time):
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, True)
    if sleep_time > 0:
        print "turn right seconds:", sleep_time
        time.sleep(sleep_time)
        stop()
    else:
        print "turn right seconds:",UNLIMITED_TIME 
        time.sleep(UNLIMITED_TIME)
        stop()

def t_test(sleep_time):
    print "test"
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)
    if sleep_time > 0:
        time.sleep(sleep_time)
        stop()

if __name__=="__main__":
    while True:
            up(5)
            down(5)
            stop()

