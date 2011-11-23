#!/bin/bash
#
#   Starts a main container (backup or master).
#
#   Define the required class locations.
#
classpath=external/jade-4.1/lib/jade.jar
classpath=${classpath}:external/jade-4.1/lib/commons-codec/commons-codec-1.3.jar
#
#   Start the main container using the provided configuration file.
#
java -cp ${classpath} jade.Boot -conf "${1}"
