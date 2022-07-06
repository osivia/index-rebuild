#!/bin/bash
set -e

# Portal properties
PORTAL_PROPERTIES=/home/portal/portal.properties


PUBLIC_HOST=${PUBLIC_HOST:-localhost}
MAIL_PWD=${MAIL_PWD:-}
ROUTE_ID=${ROUTE_ID:-portal-1}

if [ "$1" = "start" ]; then
    echo "Adapting portal.properties"

    sed -i s\\PUBLIC_HOST\\$PUBLIC_HOST\\g $PORTAL_PROPERTIES
    sed -i s\\MAIL_PWD\\$MAIL_PWD\\g $PORTAL_PROPERTIES
    sed -i s\\ROUTE_ID\\$ROUTE_ID\\g $PORTAL_PROPERTIES


    /opt/portal/bin/catalina.sh jpda run

fi



