#!/bin/sh 
espeak "Hello, I am Raspberry Pi, I am online now" 2>/dev/null
cd /home/pi && java -jar swagger-spring-1.0.0.jar &
espeak "Let me start the weather monitor service" 2>/dev/null
cd /home/pi/rpi/pms5003t/ && ./monitor.sh >>/var/log/rpi.log &
#cd /home/pi/rpi/tools/images && python -m SimpleHTTPServer 8081 &
cd /home/pi/rpi/tools/ && ./monitor.sh >>/var/log/rpi.log &
#cd /home/pi/rpi/tools/ && ./start_video_stream.sh>>/var/log/rpi.log &
espeak "Now everything is working perfectly" 2>/dev/null
pulseaudio -D &
espeak "Enjoy your time now"

# Auto start the Car
espeak "Start the car now"
cd /home/pi/rpi/car/ && python car.py>>/var/log/rpi.log &

sleep_second=6
echo "`date` sleep ${sleep_second} seconds" >>/var/log/rpi.log
sleep ${sleep_second} 
#ssh -gNfR ef.pjq.me:12222:localhost:22 pjq@ef.pjq.me 
#ssh -gNfR ef.pjq.me:18080:localhost:8080 pjq@ef.pjq.me 
#ssh -gNfR ef.pjq.me:18081:localhost:8081 pjq@ef.pjq.me 
#cd /home/pi/rpi/tools/ && ./autoforward.sh>>/var/log/rpi.log
echo "`date` finish start ssh forward" >>/var/log/rpi.log
