#!/usr/bin/python  
# coding=utf-8

import urllib
import urllib.request as urllib2

import json


def post(url, data):
    req = urllib2.Request(url)
    req.add_header('Content-Type', 'application/json')

    data = json.dumps(data)
    print(url)
    print(data)
    # enable cookie
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
    response = opener.open(req, data)
    return response.read()


def post2(url, data):
    data = json.dumps(data)
    print(url)
    print(data)
    req = urllib2.Request(url, data)
    req.add_header('Content-Type', 'application/json')
    response = urllib2.urlopen(req)
    return response.read()


def post_ubidots(url, data):
    req = urllib2.Request(url)
    req.add_header('Content-Type', 'application/json')
    req.add_header('X-Auth-Token', 'R2Flv8caVaT0cE6cVZivS9Rs3zTiHf')

    data = json.dumps(data)
    # enable cookie
    opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
    response = opener.open(req, data, timeout=10)
    return response.read()


def main():
    posturl = "http://127.0.0.1:8080/api/weather"
    data = {"pm25": 150, "pm25_cf": 27, "pm10": 25, "pm10_cf": 25, "temperature": 23.2, "humidity": 0.2,
            "raw_data": "string", "location": "home", "alt": 0, "lat": 0}
    print(post2(posturl, data))


if __name__ == '__main__':
    main()
