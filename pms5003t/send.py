#!/usr/bin/python  
#coding=utf-8  
  
import urllib  
import urllib2  
import json
  
def post(url, data):  
    req = urllib2.Request(url)  
    req.add_header('Content-Type', 'application/json')

    data = json.dumps(data)
    print data
    #enable cookie  
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())  
    response = opener.open(req, data)  
    return response.read()  
  
def main():  
    posturl = "http://10.129.36.206:8080/api/rpi/weather"
    data = { "id": 2, "pm25": 28, "pm25_cf": 27, "pm10": 25, "pm10_cf": 25, "temperature": 23.2, "humidity": 0.2, "raw_data": "string", "location": "home", "alt": 0, "lat": 0 }
    print post(posturl, data)  
  
if __name__ == '__main__':  
    main()  
