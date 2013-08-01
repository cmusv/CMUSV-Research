#!/bin/bash
#
#   This script provides a workaround to a problem with Glassfish logging (bug 17037)
#
/opt/eclipse-juno/plugins/oracle.eclipse.runtime.glassfish.build312_1.0.0/glassfish3/bin/asadmin set-log-levels com.sun.enterprise.server.logging.GFFileHandler=ALL
