#!/bin/bash
set -e

# Portal properties
PORTAL_PROPERTIES=/portal.properties


PUBLIC_HOST=${PUBLIC_HOST:-localhost}
MAIL_PWD=${MAIL_PWD:-}


echo "Adapting portal.properties"

sed -i s\\PUBLIC_HOST\\$PUBLIC_HOST\\g $PORTAL_PROPERTIES
sed -i s\\MAIL_PWD\\$MAIL_PWD\\g $PORTAL_PROPERTIES

/usr/local/tomcat/bin/catalina.sh jpda run

