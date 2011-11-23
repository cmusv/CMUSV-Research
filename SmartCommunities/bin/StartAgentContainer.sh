#!/bin/bash
#
#   Starts the SmartCommunities agent(s).
#
#   Define the required class locations.
#
classpath=classes
classpath=${classpath}:external/jade-4.1/lib/jade.jar
classpath=${classpath}:external/jade-4.1/lib/commons-codec/commons-coded-1.3.jar
classpath=${classpath}:external/slf4j-1.6.4/slf4j-api-1.6.4.jar
#classpath=${classpath}:external/slf4j-1.6.4/slf4j-jdk14-1.6.4.jar
classpath=${classpath}:external/slf4j-1.6.4/slf4j-log4j12-1.6.4.jar
classpath=${classpath}:external/apache-log4j-1.2.16/log4j-1.2.16.jar
#
#   Define the possible hosts for the main container.
#
#hostList="mcsmith"
#
#   Define the agents to be started.
#
#agentList="ReceiverAgent:edu.cmu.smartcommunities.ReceiverAgent(ReceiverAgent.properties)"
#agentList="${agentList};SenderAgent:edu.cmu.smartcommunities.SenderAgent(SenderAgent.properties)"
#
#   Try to start the agents while discovering an existing main container.
#
#mainContainerDiscovered=false
#for host in ${hostList}
#do
#   java -cp ${classpath} jade.Boot -container -host ${host} ${agentList}
#   if [ ${?} -eq 0 ]; then
#      mainContainerDiscovered=true
#   fi
#done
#
#   If no main container was found, start one locally.
#
java -cp ${classpath} jade.Boot -conf scenarios/SenderAndReceiver/AgentContainer.properties
