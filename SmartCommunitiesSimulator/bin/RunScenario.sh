#!/bin/bash
#
#   This scripts runs a JADE scenario.
#
#   Verify that a scenario name is provided.
#
if [ ${#} -eq 1 ]
then
   scenario="${1}"
   #
   #   Verify that the scenario directory exists.
   #
   if [ -d scenarios/"${scenario}" ]
   then
      #
      #   Verify that the environment configuration script exists.
      #
      if [ -r scenarios/"${scenario}"/ConfigureEnvironment.sh ]
      then
         #
         #   Load the environment configuration.
         #
         . scenarios/"${scenario}"/ConfigureEnvironment.sh
         #
         #   Start the master or backup main container, as appropriate.
         #
         export HOSTNAME=$(hostname)
         if [ ${MasterMainContainerHost} = ${HOSTNAME} ]
         then
            bin/StartMainContainer.sh scenarios/"${scenario}"/MasterMainContainer.properties &
            export ProximalMainContainerHost=${MasterMainContainerHost}
            export ProximalMainContainerPort=${MasterMainContainerPort}
         else
            bin/StartMainContainer.sh scenarios/"${scenario}"/BackupMainContainer.properties &
            export ProximalMainContainerHost=${BackupMainContainerHost}
            export ProximalMainContainerPort=${BackupMainContainerPort}
         fi
         sleep 5
         #
         #   Start the sniffer, if necessary.
         #
         #bin/StartSnifferContainer.sh scenarios/"${scenario}"/SnifferContainer.properties &
         #sleep 2
         #
         #   Start the peripheral container.
         #
         bin/StartPeripheralContainer.sh scenarios/"${scenario}"/PeripheralContainer.properties &
      else
         echo The file scenarios/${scenario}/ConfigureEnvironment.sh does not exist.
         exit 1
      fi
   else
      echo The scenario directory scenarios/"${scenario}" does not exist.
      exit 1
   fi
else
   echo Usage:  $(basename ${0}) scenarioName
   exit 1
fi
