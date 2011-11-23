#!/bin/bash
#
#   This script creates a distribution package for SmartCommunities.
#
tarContents="bin"
tarContents="${tarContents} classes"
tarContents="${tarContents} external/apache-log4j-1.2.16/log4j-1.2.16.jar"
tarContents="${tarContents} external/jade-4.1/lib/jade.jar"
tarContents="${tarContents} external/jade-4.1/lib/commons-codec/commons-codec-1.3.jar"
tarContents="${tarContents} external/slf4j-1.6.4/slf4j-api-1.6.4.jar"
tarContents="${tarContents} external/slf4j-1.6.4/slf4j-log4j12-1.6.4.jar"
tarContents="${tarContents} scenarios"
tar -cvf SmartCommunities.tar ${tarContents}
mv SmartCommunities.tar /media/PENDRIVE/
sync
sync
sync
