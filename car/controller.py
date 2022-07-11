#!/usr/bin/env python3
import RPi.GPIO as GPIO
import time
import config as CFG

GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)

PIN1 = CFG.WHEEL_PIN1
PIN2 = CFG.WHEEL_PIN2
PIN3 = CFG.WHEEL_PIN3
PIN4 = CFG.WHEEL_PIN4
ENA = CFG.WHEEL_ENA
ENB = CFG.WHEEL_ENB

# print( PIN1,PIN2,PIN3,PIN4)
UNLIMITED_TIME = CFG.WHEEL_UNLIMITED_TIME
FREQ = CFG.WHEEL_FREQ
DUTY = CFG.WHEEL_DUTY

GPIO.setup(PIN1, GPIO.OUT)
GPIO.setup(PIN2, GPIO.OUT)
GPIO.setup(PIN3, GPIO.OUT)
GPIO.setup(PIN4, GPIO.OUT)
# GPIO.setup(7,GPIO.IN)

GPIO.setup(ENA, GPIO.OUT)
GPIO.setup(ENB, GPIO.OUT)
pa = GPIO.PWM(ENA, FREQ)
pb = GPIO.PWM(ENB, FREQ)

# Motion detect
MOTION_PIN = CFG.MOTION_PIN
GPIO.setup(MOTION_PIN, GPIO.IN)


def pwm_init():
    pa.start(DUTY)
    pb.start(DUTY)


pwm_init()


def pwm_change_duty(duty):
    print("duty: ", duty)
    pa.ChangeDutyCycle(duty)
    pb.ChangeDutyCycle(duty)
    # time.sleep(2)


def go_stop():
    print("go stop")
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, False)
    # pa.stop()
    # pb.stop()


def stop(sleep_time):
    print("stop")
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, False)
    time.sleep(sleep_time)


def go_down():
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)


def down(sleep_time, s):
    pwm_change_duty(s)
    go_down()
    if sleep_time > 0:
        print("backward seconds:", sleep_time)
        time.sleep(sleep_time)
        go_stop()
    else:
        print("backward seconds:", UNLIMITED_TIME)
        time.sleep(UNLIMITED_TIME)
        go_stop()


def go_up():
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, True)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, True)


def up(sleep_time, s):
    pwm_change_duty(s)
    go_up()
    if sleep_time > 0:
        print("forward seconds:", sleep_time)
        time.sleep(sleep_time)
        go_stop()
    else:
        print("forward seconds:", UNLIMITED_TIME)
        time.sleep(UNLIMITED_TIME)
        go_stop()


def go_left():
    GPIO.output(PIN1, False)
    GPIO.output(PIN2, True)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)


def left(sleep_time, s):
    pwm_change_duty(s)
    go_left()
    if sleep_time > 0:
        print("turn left seconds:", sleep_time)
        time.sleep(sleep_time)
        go_stop()
    else:
        print("turn left seconds:", UNLIMITED_TIME)
        time.sleep(UNLIMITED_TIME)
        go_stop()


def go_right():
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, False)
    GPIO.output(PIN4, True)


def right(sleep_time, s):
    pwm_change_duty(s)
    go_right()
    if sleep_time > 0:
        print("turn right seconds:", sleep_time)
        time.sleep(sleep_time)
        go_stop()
    else:
        print("turn right seconds:", UNLIMITED_TIME)
        time.sleep(UNLIMITED_TIME)
        go_stop()


def t_test(sleep_time):
    print("test")
    GPIO.output(PIN1, True)
    GPIO.output(PIN2, False)
    GPIO.output(PIN3, True)
    GPIO.output(PIN4, False)
    if sleep_time > 0:
        time.sleep(sleep_time)
        go_stop()


def motion_detect():
    return GPIO.input(MOTION_PIN) == True


if __name__ == "__main__":
    pwm(0.1)
    while True:
        up(5)
        down(5)
        go_stop()
