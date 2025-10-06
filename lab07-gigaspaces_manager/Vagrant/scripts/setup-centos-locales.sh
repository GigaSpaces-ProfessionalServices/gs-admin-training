#!/bin/bash

function setupLocales {
	echo "modifying /etc/environment file"
	echo "LANG=en_US.utf-8" >> /etc/environment
	echo "LC_ALL=en_US.utf-8" >> /etc/environment 
}


echo "setup centos locales environment file"
setupLocales
