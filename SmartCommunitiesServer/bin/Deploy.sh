#!/bin/bash
#
#   Replace this with an ANT file.
#
#   Temporary constraint:  Execute only after a clean build within Eclipse.
#
cd WebContent
rm -rf WEB-INF/classes
cp -pr ../build/classes WEB-INF
jar -cvf ../SmartCommunitiesTestWeb.war .
scp ../SmartCommunitiesTestWeb.war tesla:.
