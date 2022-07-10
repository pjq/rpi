#!/usr/bin/env python3 
import RPi.GPIO as GPIO
import time
import sys
import ultrasound
import controller
import obstacle as obst

# minimum distance(cm)
min_distance = 50
max_distance = 100
duty = 25

count = 0
back_time = 0.5
turn_time = 0.2
if __name__ == "__main__":
    if len(sys.argv) == 3:
        sleep = float(sys.argv[1])
        duty = float(sys.argv[2])
    print("duty: ", duty)
    controller.pwm_change_duty(duty)
    time.sleep(0.05)
    controller.go_up()
    try:
        while True:
            # distance=ultrasound.get_distance()
            distance = 90
            back_obstacle1 = obst.has_obstacle(obst.PIN1)
            back_obstacle2 = obst.has_obstacle(obst.PIN2)
            back_obstacle3 = obst.has_obstacle(obst.PIN3)
            back_obstacle4 = obst.has_obstacle(obst.PIN4)
            # disable the left/right sensors for auto track
            back_obstacle1 = False
            back_obstacle4 = False
            left = back_obstacle1
            middle_left = back_obstacle2
            middle_right = back_obstacle3
            right = back_obstacle4

            obstacle = back_obstacle1 or back_obstacle3 or back_obstacle4

            count += 1
            print("%s:distance: %s (cm), has back obstacle, left:%s middle_left:%s middle_right:%s right:%s" % (
            count, distance, left, middle_left, middle_right, right))

            if ((left == True) and (right == True)) or ((middle_left == True) and (middle_right == True)):
                controller.go_down()
                time.sleep(back_time)
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()

            if middle_left == True:
                controller.go_down()
                time.sleep(back_time)
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()
            elif middle_right == True:
                controller.go_down()
                time.sleep(back_time)
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()
            elif left == True:
                controller.go_down()
                time.sleep(back_time)
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()
            elif right == True:
                controller.go_down()
                time.sleep(back_time)
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()

            # if distance <  min_distance:
            #    print( "backward")
            #    controller.go_down()

            # if distance > max_distance:
            #    print( "forward")
            #    controller.go_up()

            time.sleep(0.08)
    except KeyboardInterrupt:
        print("KeyboardInterrupt")
        controller.go_stop()
