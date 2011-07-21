#!/bin/bash

#This script is used to kill the REmail daemon if necessary

#Some variables
installationDirectory='./REmail'
logDirectory=$installationDirectory'/log'
lockFile=$installationDirectory'/REmail_lock'

pid=`cat $lockFile`

if [[ $pid == "" ]]; then
    echo "Daemon is not running"
else
    kill -9 $pid
    echo "Daemon killed"
    echo "" > $lockFile
fi