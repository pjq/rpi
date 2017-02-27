#### GPIO Python

Refer http://www.cnblogs.com/rainduck/archive/2012/09/22/2694568.html

1. Download Rpi.GPIO http://pypi.python.org/pypi/RPi.GPIO/
2. sudo apt-get install python-dev

```
  tar xvzf RPi.GPIO-0.x.xx.tar.gz
  cd RPi.GPIO-0.x.xx
  sudo python setup.py install
  sudo easy_install -U RPIO
  sudo rpio -I
```


####PMS5003T
Dump the binary data with od

The hex data will be like as the following format
```
#ttyAMA0:0042 004d 0014 0022 0033
#ttyUSB0:4d42 1400 2500 2f00
```

##### Debug the hex data
```
sudo od /dev/ttyAMA0
sudo od -Ax -tcx1 /dev/ttyAMA0
sudo xxd /dev/ttyAMA0
```

The hex data will be like
```
00 42
00 4d
00 1c
00 0a
00 0f
00 10
00 0a
00 0f
00 10
08 5e
02 6c
00 4b
00 05
00 e7
01 05
91 00
03 9f
1c 00
0a 00
0f 00
10 00
```
##### Run PMS5003T Script

Run the python script, and it will print the details
```
  cd pms5003t
  sudo ./g5.py
```
And the result will be like
```
  pm2.5: 23 pm2.5(cf): 23 pm1.0: 17 pm10: 23 temp(c): 22.9 humi(%): 23.5 version: 145 error: 0
```
