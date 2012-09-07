#!/bin/bash
#
#   This script configures environment variables for the SenderAndReceiver scenario.
#
. scenarios/Common/ConfigureEnvironment.sh

export BackupMainContainerHost=${HOSTNAME}
export BackupMainContainerPort=1234
export MasterMainContainerHost=community0node0
export MasterMainContainerPort=1099
