#!/bin/bash
#
#   This script starts the JADE sniffer agent.
#
if [ -f $(dirname "${1}")/sniffer.properties ]
then
   cp -p $(dirname "${1}")/sniffer.properties .
fi
java -cp ${JadeClasspath} jade.Boot -conf "${1}"
