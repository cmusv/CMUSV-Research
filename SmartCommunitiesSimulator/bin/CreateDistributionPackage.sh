#!/bin/bash
#
#   This script creates a distribution package for SmartCommunities.
#
ExternalDependenciesHome=external
JadeHome=${ExternalDependenciesHome}/jade-4.1.1
Log4jHome=${ExternalDependenciesHome}/apache-log4j-1.2.16
Slf4jHome=${ExternalDependenciesHome}/slf4j-1.6.4
tarContents="bin"
tarContents="${tarContents} classes"
tarContents="${tarContents} ${Log4jHome}/log4j-1.2.16.jar"
tarContents="${tarContents} ${JadeHome}/add-ons/security/lib/jadeSecurity.jar"
tarContents="${tarContents} ${JadeHome}/lib/jade-20111212.jar"
tarContents="${tarContents} ${JadeHome}/lib/commons-codec/commons-codec-1.3.jar"
tarContents="${tarContents} ${Slf4jHome}/slf4j-api-1.6.4.jar"
tarContents="${tarContents} ${Slf4jHome}/slf4j-log4j12-1.6.4.jar"
tarContents="${tarContents} scenarios"
#
#   Create the distribution package
#
tar -cvf SmartCommunities.tar ${tarContents}
mv SmartCommunities.tar /media/5E87-737A/
sync
sync
sync
