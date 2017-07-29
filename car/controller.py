#!/user/bin/env python 
import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

PIN1=11
PIN2=12
PIN3=15
PIN4=16 

print PIN1,PIN2,PIN3,PIN4

GPIO.setup(PIN1,GPIO.OUT)
GPIO.setup(PIN2,GPIO.OUT)
GPIO.setup(PIN3,GPIO.OUT)
GPIO.setup(PIN4,GPIO.OUT)
#GPIO.setup(7,GPIO.IN)

def t_stop():
    print "stop"
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, False)

def t_down():
    print "go down"
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)

def t_up():
    print "go up"
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, True)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, True)

def t_left():
    print "turn left"
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, True)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)

def t_right():
    print "turn right"
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, True)

def t_test():
    print "test"
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)

if __name__=="__main__":
    while True:
            #t_left()
            #time.sleep(5)
            #t_right()
            #time.sleep(5)
            t_up()
            time.sleep(5)
            t_down()
            time.sleep(5)
            t_stop()

