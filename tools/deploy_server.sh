#!/bin/bash
echo `date` "deploy server"
cat<<EOF
cd ~/rpi/spring-server
git pull origin master
mvn package
sudo pkill java
cp target/swagger-spring-1.0.0.jar ../target/ 
cd ../target
java -jar swagger-spring-1.0.0.jar 
EOF

cd ~/rpi/spring-server
git pull origin master
mvn package
sudo pkill java
cp target/swagger-spring-1.0.0.jar ../target/ 
cd ../target
java -jar swagger-spring-1.0.0.jar 

