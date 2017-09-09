#!/usr/bin/env python
import serial
import time
import sys
import send
import commands
from struct import *

posturl = "http://10.129.41.134:8080/api/rpi/weather"
posturl = "http://10.129.42.132:8080/api/rpi/weather"
posturl = "http://10.59.176.71:8080/api/rpi/weather"
posturl = "http://192.168.31.177:8080/api/rpi/weather"
#posturl = "http://192.168.31.180:8080/api/rpi/weather"
posturl = "http://127.0.0.1:8080/api/weather"
location="home"
#location="office"
#pi1
tty_device="/dev/ttyAMA0"
#pi3
tty_device="/dev/serial0"

#data = { "id": 2, "pm25": 28, "pm25_cf": 27, "pm10": 25, "pm10_cf": 25, "temperature": 23.2, "humidity": 0.2, "raw_data": "string", "location": "home", "alt": 0, "lat": 0 }
debug=0


# work for pms3003
# data structure: https://github.com/avaldebe/AQmon/blob/master/Documents/PMS3003_LOGOELE.pdf
# fix me: the format is different between /dev/ttyUSBX(USB to Serial) and /dev/ttyAMA0(GPIO RX)
#          ttyAMA0:0042 004d 0014 0022 0033
#          ttyUSB0:4d42 1400 2500 2f00
revision=commands.getoutput('cat /proc/cpuinfo |grep Revision|cut -d ":" -f2')
if  "a22082" in revision:
    tty_device="/dev/serial0"
else:
    tty_device="/dev/ttyAMA0"

if debug: print tty_device

class g3sensor():
    def __init__(self):
        if debug: print "init"
	self.endian = sys.byteorder
    
    def conn_serial_port(self, device):
        if debug: print device
        self.serial = serial.Serial(device, baudrate=9600)
        if debug: print "conn ok"

    def check_keyword(self):
        if debug: print "check_keyword"
        while True:
            token = self.serial.read()
    	    token_hex=token.encode('hex')
    	    if debug: print token_hex
    	    if token_hex == '42':
    	        if debug: print "get 42"
    	        token2 = self.serial.read()
    	        token2_hex=token2.encode('hex')
    	        if debug: print token2_hex
    	        if token2_hex == '4d':
    	            if debug: print "get 4d"
                    return True
		elif token2_hex == '00': # fixme
		    if debug: print "get 00"
		    token3 = self.serial.read()
		    token3_hex=token3.encode('hex')
		    if token3_hex == '4d':
			if debug: print "get 4d"
			return True
		    
    def vertify_data(self, data):
	if debug: print data
        n = 2
	sum = int('42',16)+int('4d',16)
        for i in range(0, len(data)-4, n):
            #print data[i:i+n]
	    sum=sum+int(data[i:i+n],16)
	versum = int(data[40]+data[41]+data[42]+data[43],16)
	if debug: print sum
        if debug: print versum
	if sum == versum:
	    print "data correct"
	
    def read_data(self):
        data = self.serial.read(30)
        data_hex=data.encode('hex')
        if debug: self.vertify_data(data_hex)
        #pm1_cf=int(data_hex[4]+data_hex[5]+data_hex[6]+data_hex[7],16)
        #pm25_cf=int(data_hex[8]+data_hex[9]+data_hex[10]+data_hex[11],16)
        #pm10_cf=int(data_hex[12]+data_hex[13]+data_hex[14]+data_hex[15],16)
        #pm1=int(data_hex[16]+data_hex[17]+data_hex[18]+data_hex[19],16)
        #pm25=int(data_hex[20]+data_hex[21]+data_hex[22]+data_hex[23],16)
        #pm10=int(data_hex[24]+data_hex[25]+data_hex[26]+data_hex[27],16)
        cycle = 4
        i = 1
        n = cycle*i 
        pm1_cf=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=2
        n = cycle*i
        pm25_cf=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=3
        n = cycle*i
        pm10_cf=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=4
        n = cycle*i
        pm1=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=5
        n = cycle*i
        pm25=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=6
        n = cycle*i
        pm10=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=7
        n = cycle*i
        pm0103=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=8
        n = cycle*i
        pm0104=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=9
        n = cycle*i
        pm0110=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=10
        n = cycle*i
        pm0125=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)

        i=11
        n = cycle*i
        temp=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)/10.0

        i=12
        n = cycle*i
        hum=int(data_hex[n]+data_hex[n+1]+data_hex[n+2]+data_hex[n+3],16)/10.0

        i=13
        n = cycle*i
        version=int(data_hex[n]+data_hex[n+1],16)
        error=int(data_hex[n+2]+data_hex[n+3],16)
        
        if 0:
	#	print "pm1_cf: "+str(pm1_cf)
	#	print "pm25_cf: "+str(pm25_cf)
	#	print "pm10_cf: "+str(pm10_cf)
	#	print "pm1: "+str(pm1)
                print time.asctime(time.localtime(time.time()))+ " pm2.5: "+str(pm25)+ " pm2.5(cf): " +str(pm25_cf) +" pm1.0: "+str(pm1) +" pm10: "+str(pm10)   + " temp(c): "+str(temp) + " humi(%): " + str(hum) + " version: " + str(version) + " error: " + str(error)
	#	print "pm10: "+str(pm10)
        date=""+time.asctime(time.localtime(time.time()))
	data={ "pm25": pm25, "pm25_cf": pm25_cf, "pm10": pm10, "pm10_cf": pm10_cf, "temperature": temp, "humidity": hum, "raw_data": "", "location": location, "alt": 0, "lat": 0, "date":date}
    	self.serial.close()

        return data

    def read(self, argv):
        tty=argv[0:]
        self.conn_serial_port(tty)
        if self.check_keyword() == True:
            self.data = self.read_data()
            if debug: print self.data
            return self.data
def sendData():
    air=g3sensor()
    pmdata=air.read(tty_device)
    print pmdata
    send.post(posturl, pmdata)

def sendUbidots(pmdata):
	#ubidots
	#POST /api/v1.6/variables/58c76d947625424c6def6f0b/values HTTP/1.1 X-Auth-Token: R2Flv8caVaT0cE6cVZivS9Rs3zTiHf Host: things.ubidots.com Connection: close Content-Type: application/json Content-Length: 14
	#{"value":1234}
	ubidots_host="http://things.ubidots.com/api/v1.6/variables/ID/values"
	pm25_url=ubidots_host.replace("ID","58c76d947625424c6def6f0b")
	pm25_index_url=ubidots_host.replace("ID","58d0ebf37625427ae806e14b")
	temp_url=ubidots_host.replace("ID","58c76d8c7625424c6a1a816e")
	humidity_url=ubidots_host.replace("ID","58c76d9c7625424c6ced252b")

        #temperature
        val=pmdata['temperature']
        if val>100:
            return

        value={"value" :val}
        send.post_ubidots(temp_url, value)

	#pm25
        val=pmdata['pm25']
        if val>1000:
            return
        value={"value" :val}
        send.post_ubidots(pm25_url, value)

	#pm25 index
        val=pmdata['pm25_cf']
        if val>1000:
            return
        value={"value" :val}
        send.post_ubidots(pm25_index_url, value)

        #humidity
        val=pmdata['humidity']
        if val>100:
            return
        value={"value" :val}
        send.post_ubidots(humidity_url, value)

def sendDatas():
    air=g3sensor()
    #pm25=15
    #value={"value":pm25}
    #send.post_ubidots(pm25_url, value)
    while True:
	pmdata=0
	try:
	   while True:
	     pmdata=air.read(tty_device)
	     send.post(posturl, pmdata)
             sendUbidots(pmdata)
	     if debug: print pmdata
             time.sleep(60)
	except: 
	    next
	if pmdata != 0:
	    #print pmdata
	    break


if __name__ == '__main__': 
    sendDatas()
