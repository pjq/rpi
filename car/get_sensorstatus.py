#!/usr/bin/env python 
import time
import sys
import os
import ultrasound 
import obstacle
import json

if __name__=="__main__":
    distance=ultrasound.get_distance()
    obstacles=obstacle.has_obstacles()
    value={"distance": distance, "obstacles":obstacles}
    value=json.dumps(value, ensure_ascii=False)
    print value

