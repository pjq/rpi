#!/usr/bin/env python 
import RPi.GPIO as GPIO
import time
import ultrasound
import controller 
import obstacle as obst

#minimum distance(cm)
min_distance=50
max_distance=100
duty=40

if __name__=="__main__":
    controller.go_up()
    controller.pwm_change_duty(duty)
    time.sleep(0.05)
    controller.go_left()
    time.sleep(0.5)
    controller.go_right()
    time.sleep(0.5)
    try:
        while True:
            distance=ultrasound.get_distance()
            back_obstacle1=obst.has_obstacle(obst.PIN1)
            back_obstacle2=obst.has_obstacle(obst.PIN2)
            back_obstacle3=obst.has_obstacle(obst.PIN3)
            back_obstacle4=obst.has_obstacle(obst.PIN4)

            obstacle=back_obstacle1 or  back_obstacle3 or back_obstacle4 

            print "distance: %s (cm), has back obstacle: %s" %(distance, obstacle)

            if obstacle == True:
                print "forward"
                controller.go_up()

            if distance <  min_distance:
                print "backward"
                controller.go_down()

            if distance > max_distance:
                print "forward"
                controller.go_up()

            time.sleep(0.02)
    except KeyboardInterrupt:
            print "KeyboardInterrupt"
            controller.go_stop()



