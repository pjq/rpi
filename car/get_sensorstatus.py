#!/usr/bin/env python 
import time
import sys
import os
import ultrasound 
import obstacle
import relay_control 
import controller 
import json
sys.path.append("../pms5003t")
import weather_sensor

if __name__=="__main__":
    distance=ultrasound.get_distance()
    obstacles=obstacle.has_obstacles()
    relay_status=relay_control.relay_status()
    motion=controller.motion_detect()
    #pm25=weather_sensor.readData()
    pm25=None
    value={"distance": distance, "obstacles":obstacles, "relay_on": relay_status, "motion_detected":motion, "weather":pm25}
    value=json.dumps(value, ensure_ascii=False)
    print value

