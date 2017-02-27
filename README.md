#### GPIO Python

Refer http://www.cnblogs.com/rainduck/archive/2012/09/22/2694568.html

1. Download Rpi.GPIO http://pypi.python.org/pypi/RPi.GPIO/
2. sudo apt-get install python-dev

  tar xvzf RPi.GPIO-0.x.xx.tar.gz
  
  cd RPi.GPIO-0.x.xx
  
  sudo python setup.py install

  sudo easy_install -U RPIO

  sudo rpio -I


####PMS5003T
Dump the binary data with od
#ttyAMA0:0042 004d 0014 0022 0033
#ttyUSB0:4d42 1400 2500 2f00

sudo od /dev/ttyAMA0
