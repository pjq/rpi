#!/bin/bash

if [ $# = 2 ];then
    localport=$1
    remoteport=$2
    #echo ssh -gNfR ef.pjq.me:${remoteport}:localhost:${localport} pjq@ef.pjq.me
    #ssh -gNfR ef.pjq.me:${remoteport}:localhost:${localport} pjq@ef.pjq.me
    echo autossh -f -M "$1"  -NR ef.pjq.me:${remoteport}:localhost:${localport} pjq@ef.pjq.me
    autossh -f -M "$1" -NR  ef.pjq.me:${remoteport}:localhost:${localport} pjq@ef.pjq.me
    echo DONE, Now you can visit it via
    echo http://ef.pjq.me:${remoteport}
else
cat <<EOF
    Usage: 
    ./$0 localport remoteport
    For example, forward the localhost port 80 to the remote server 8080
    ssh -gNfR ef.pjq.me:8080:localhost:80 pjq@ef.pjq.me
    Then, you can visit it via: http://ef.pjq.me:8080
EOF

fi


