#!/bin/bash
#
#   Starts a (backup or master) main container using the provided configuration.
#
java -cp ${JadeClasspath} -DJade.jar=${JadeJar} -DJadeSecurity.jar=${JadeSecurityJar} jade.Boot -conf "${1}"
