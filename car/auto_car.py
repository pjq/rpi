#!/usr/bin/env python 
import RPi.GPIO as GPIO
import time
import sys
import ultrasound
import controller 
import obstacle as obst

#minimum distance(cm)
min_distance=50
max_distance=100
duty=10

count=0
back_time=0.1
turn_time=0.5
if __name__=="__main__":
    if len(sys.argv)==3:
       sleep=float(sys.argv[1])
       duty=float(sys.argv[2])
    print "duty: ", duty
    controller.pwm_change_duty(duty)
    time.sleep(0.05)
    controller.go_up()
    try:
        while True:
            distance=ultrasound.get_distance()
            back_obstacle1=obst.has_obstacle(obst.PIN1)
            back_obstacle2=obst.has_obstacle(obst.PIN2)
            back_obstacle3=obst.has_obstacle(obst.PIN3)
            back_obstacle4=obst.has_obstacle(obst.PIN4)

            left=back_obstacle1
            middle_left=back_obstacle2
            middle_right=back_obstacle3
            right=back_obstacle4

            obstacle=back_obstacle1 or  back_obstacle3 or back_obstacle4 

            count+=1
            print "%s:distance: %s (cm), has back obstacle, left:%s middle_left:%s middle_right:%s right:%s" %(count, distance, left, middle_left, middle_right, right)
            print "%s left:%s  right:%s" %(count, left, right)

            if distance <  min_distance:
                print "backward"
                controller.go_down()

            #if distance > max_distance:
            #    print "forward"
            #    controller.go_up()

            if  ((left == False) and (right == False)) :
                controller.go_left()
                time.sleep(turn_time*4)
                while True == obst.has_obstacle(obst.PIN1):
                    controller.go_left()
                    time.sleep(turn_time/4)
                time.sleep(turn_time)
                controller.go_up()

            if  ((middle_left == True) or (middle_right == True)) :
                controller.go_down()
                time.sleep(back_time)
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()

                
            if left == False:
                print "turn left"
                controller.go_left()
                time.sleep(turn_time)
                controller.go_up()

            if right == False:
                print "turn right"
                controller.go_right()
                time.sleep(turn_time)
                controller.go_up()


            time.sleep(0.05)
    except KeyboardInterrupt:
            print "KeyboardInterrupt"
            controller.go_stop()



