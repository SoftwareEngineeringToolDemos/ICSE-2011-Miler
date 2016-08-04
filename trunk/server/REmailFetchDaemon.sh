#!/bin/bash

#This script creates a daemon which fetch the emails using POPclient.py

#Some variables
installationDirectory='./REmail'
logDirectory=$installationDirectory'/log'
lockFile=$installationDirectory'/REmail_lock'
sleepTime=5 #in minutes

#Try to acquire the log
lock=`cat $lockFile`
if [[ $lock == '' ]]; then
    #lock the file
    echo $$ > $lockFile
    #do the job!
    while [ true ]; do
	time=`date -u +%Y-%m-%d`
	at=`date -u +%H:%M`
	echo "Request done at: "$at >> $logDirectory'/log_'$time
	python POPclient.py >> $logDirectory'/log_'$time
	echo "-------------------------------" >> $logDirectory'/log_'$time
	let t=$sleepTime*60
	sleep $t
    done
else
    echo "Daemon is already running"
fi



	