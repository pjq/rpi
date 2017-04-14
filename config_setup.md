# brief list what I did to make the raspberry pi 3 running. 

# config the sources.list
cd /etc/apt/
sudo cp sources.list sources.list.bak
sudo vi sources.list
# deb http://mirrors.aliyun.com/raspbian/raspbian/ jessie main contrib non-free rpi
cd sources.list.d/
sudo cp raspi.list raspi.list.bak
sudo vi raspi.list
sudo apt-get update

# install the necessary software
sudo apt-get install wget vim subversion git mpg123 screen &&  wget https://raw.githubusercontent.com/pjq/config/master/.vimrc -O ~/.vimrc



# run the pms5003t service, check http://rpi.pjq.me
git clone https://github.com/pjq/rpi.git
cd rpi/
cd pms5003t/
sudo ./monitor.sh

# make it as the music player 
scp netease/* pi@192.168.31.135:/home/pi/Music
sudo raspi-config
alsamixer
cd Music
mpg123 -C *.mp3

# install mysql
sudo apt-get install mysql-server
mysql -uroot -p
create database rpi;

# auto start the weather monitor when boot the system, add to /etc/rc.local
cd /home/pi && java -jar swagger-spring-1.0.0.jar &
cd /home/pi/rpi/pms5003t/ && ./monitor.sh >>/var/log/rpi.log &

