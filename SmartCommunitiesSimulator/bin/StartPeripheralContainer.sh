#!/bin/bash
#
#   Starts the SmartCommunities agent(s).
#
SmartCommunitiesDatabaseHome=../SmartCommunitiesDatabase
#HibernateHome=${SmartCommunitiesDatabaseHome}/external/hibernate-distribution-3.6.0.Final
#MysqlHome=${SmartCommunitiesDatabaseHome}/external/mysql-connector-java-5.0.8
Slf4jHome=external/slf4j-1.6.4
Log4jHome=external/apache-log4j-1.2.16
#
#   Define the required class locations.
#
AgentClasspath=classes
AgentClasspath=${AgentClasspath}:${SmartCommunitiesDatabaseHome}/classes
#AgentClasspath=${AgentClasspath}:${HibernateHome}/hibernate3.jar
#AgentClasspath=${AgentClasspath}:${HibernateHome}/lib/jpa/hibernate-jpa-2.0-api-1.0.0.Final.jar
#AgentClasspath=${AgentClasspath}:${HibernateHome}/lib/required/antlr-2.7.6.jar
#AgentClasspath=${AgentClasspath}:${HibernateHome}/lib/required/commons-collections-3.1.jar
#AgentClasspath=${AgentClasspath}:${HibernateHome}/lib/required/dom4j-1.6.1.jar
#AgentClasspath=${AgentClasspath}:${HibernateHome}/lib/required/javassist-3.12.0.GA.jar
#AgentClasspath=${AgentClasspath}:${HibernateHome}/lib/required/jta-1.1.jar
#AgentClasspath=${AgentClasspath}:${MysqlHome}/mysql-connector-java-5.0.8-bin.jar
AgentClasspath=${AgentClasspath}:${Slf4jHome}/slf4j-api-1.6.4.jar
#AgentClasspath=${AgentClasspath}:${Slf4jHome}/slf4j-jdk14-1.6.4.jar
AgentClasspath=${AgentClasspath}:${Slf4jHome}/slf4j-log4j12-1.6.4.jar
AgentClasspath=${AgentClasspath}:${Log4jHome}/log4j-1.2.16.jar
#
#   Start the peripheral container using the provided configuration file.
#
java -cp ${JadeClasspath}:${AgentClasspath} -DJade.jar=${JadeJar} -DJadeSecurity.jar=${JadeSecurityJar} jade.Boot -conf "${1}"
