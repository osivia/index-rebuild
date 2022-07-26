#!/bin/bash
set -e

# Portal properties
PORTAL_PROPERTIES=/home/portal/portal.properties


PUBLIC_HOST=${PUBLIC_HOST:-localhost}
MAIL_PWD=${MAIL_PWD:-}
ROUTE_ID=${ROUTE_ID:-portal-1}
NODE_1=${NODE_1:-portal-1}
NODE_2=${NODE_2:-portal-2}

PRONOTE_ETB_SERVICE_URL_GET=${PRONOTE_ETB_SERVICE_URL_GET:-}
PRONOTE_ETB_SERVICE_URL_CHECK=${PRONOTE_ETB_SERVICE_URL_CHECK:-}


if [ "$1" = "start" ]; then
    echo "Adapting portal.properties"

    sed -i s\\PUBLIC_HOST\\$PUBLIC_HOST\\g $PORTAL_PROPERTIES
    sed -i s\\MAIL_PWD\\$MAIL_PWD\\g $PORTAL_PROPERTIES
    sed -i s\\ROUTE_ID\\$ROUTE_ID\\g $PORTAL_PROPERTIES
    sed -i s\\NODE_1\\$NODE_1\\g $PORTAL_PROPERTIES
    sed -i s\\NODE_2\\$NODE_2\\g $PORTAL_PROPERTIES
	sed -i s\\PRONOTE_ETB_SERVICE_URL_GET\\$PRONOTE_ETB_SERVICE_URL_GET\\g $PORTAL_PROPERTIES		
	sed -i s\\PRONOTE_ETB_SERVICE_URL_CHECK\\$PRONOTE_ETB_SERVICE_URL_CHECK\\g $PORTAL_PROPERTIES	    

    /opt/portal/bin/catalina.sh jpda run

fi



