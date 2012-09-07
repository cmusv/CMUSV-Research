#!/bin/bash
#
#   Starts the SmartCommunities agent(s).
#
#   Define the required class locations.
#
AgentClasspath=build/classes
AgentClasspath=${AgentClasspath}:build/lib/SmartCommunitiesDatabase.jar
AgentClasspath=${AgentClasspath}:build/lib/antlr-2.7.7.jar
AgentClasspath=${AgentClasspath}:build/lib/c3p0-0.9.1.jar
AgentClasspath=${AgentClasspath}:build/lib/dom4j-1.6.1.jar
AgentClasspath=${AgentClasspath}:build/lib/hibernate-c3p0-4.1.4.Final.jar
AgentClasspath=${AgentClasspath}:build/lib/hibernate-commons-annotations-4.0.1.Final.jar
AgentClasspath=${AgentClasspath}:build/lib/hibernate-core-4.1.4.Final.jar
AgentClasspath=${AgentClasspath}:build/lib/hibernate-jpa-2.0-api-1.0.1.Final.jar
AgentClasspath=${AgentClasspath}:build/lib/javassist-3.15.0-GA.jar
AgentClasspath=${AgentClasspath}:build/lib/jboss-logging-3.1.0.GA.jar
AgentClasspath=${AgentClasspath}:build/lib/jboss-transaction-api_1.1_spec-1.0.0.Final.jar
AgentClasspath=${AgentClasspath}:build/lib/log4j-1.2.16.jar
AgentClasspath=${AgentClasspath}:build/lib/mysql-connector-java-5.1.20-bin.jar
AgentClasspath=${AgentClasspath}:build/lib/slf4j-api-1.6.4.jar
AgentClasspath=${AgentClasspath}:build/lib/slf4j-log4j12-1.6.4.jar
#
#   Start the peripheral container using the provided configuration file.
#
java -cp ${JadeClasspath}:${AgentClasspath} -DJade.jar=${JadeJar} -DJadeSecurity.jar=${JadeSecurityJar} jade.Boot -conf "${1}"
