#!/bin/bash
#
#   This script configures environment variables for the EnergyUsageSimulation scenario.
#
. scenarios/Common/ConfigureEnvironment.sh

export BackupMainContainerHost=$(hostname)
export BackupMainContainerPort=1234
export MasterMainContainerHost=$(hostname)
export MasterMainContainerPort=1099
