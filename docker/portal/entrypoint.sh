#!/bin/bash
set -e

# Portal properties
PORTAL_PROPERTIES=/portal.properties

#PRONOTE Secrets
PUBLIC_HOST=${PUBLIC_HOST:-localhost}


echo "Adapting portal.properties"

sed -i s\\PUBLIC_HOST\\$PUBLIC_HOST\\g $PORTAL_PROPERTIES


/usr/local/tomcat/bin/catalina.sh jpda run

