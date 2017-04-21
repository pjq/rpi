#!/bin/bash
echo "raspivid -t 0 -w 1280 -h 720 -hf -ih -fps 20 -o - | nc -k -l 8084"
raspivid -t 0 -w 1280 -h 720 -hf -ih -fps 20 -o - | nc -k -l 8084 &

echo "mplayer -fps 20 -demuxer h264es ffmpeg://tcp://10.58.103.91:8084"
#mplayer -fps 20 -demuxer h264es ffmpeg://tcp://10.58.103.91:8084
