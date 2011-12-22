#!/bin/bash
#
#   Starts the SmartCommunities agent(s).
#
Log4jHome=external/apache-log4j-1.2.16
Slf4jHome=external/slf4j-1.6.4
#
#   Define the required class locations.
#
AgentClasspath=classes
AgentClasspath=${AgentClasspath}:${Slf4jHome}/slf4j-api-1.6.4.jar
#AgentClasspath=${AgentClasspath}:${Slf4jHome}/slf4j-jdk14-1.6.4.jar
AgentClasspath=${AgentClasspath}:${Slf4jHome}/slf4j-log4j12-1.6.4.jar
AgentClasspath=${AgentClasspath}:${Log4jHome}/log4j-1.2.16.jar
#
#   Start the peripheral container using the provided configuration file.
#
java -cp ${JadeClasspath}:${AgentClasspath} -DJade.jar=${JadeJar} -DJadeSecurity.jar=${JadeSecurityJar} jade.Boot -conf "${1}"
