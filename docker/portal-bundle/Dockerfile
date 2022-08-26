FROM openjdk:8-jdk
MAINTAINER Cédric Krommenhoek <ckrommenhoek@osivia.com>

RUN apt-get update && apt-get install -y locales vim unzip \
	&& localedef -i fr_FR -c -f UTF-8 -A /usr/share/locale/locale.alias fr_FR.UTF-8 \
	&& apt-get purge -y \
    && rm -rf /var/lib/apt/lists/*

ENV LC_ALL fr_FR.utf8
RUN locale >> /etc/default/locale

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV TOMCAT_ROOT /opt/portal/webapps/

ENV CATALINA_HOME /opt/portal
ENV PATH $CATALINA_HOME/bin:$PATH

ENV PORTAL_VERSION=5.2-SNAPSHOT
ENV PORTAL_DISTRIBUTION_NAME=index-cloud-ens-portal-distribution-bundle
ENV PORTAL_DISTRIBUTION ${PORTAL_DISTRIBUTION_NAME}-${PORTAL_VERSION}
ENV PORTAL_DISTRIBUTION_ZIP ${PORTAL_DISTRIBUTION_NAME}-${PORTAL_VERSION}.zip


RUN mkdir -p "/home/portal"
RUN mkdir -p "/var/log/portal"


# Distribution
COPY ${PORTAL_DISTRIBUTION_ZIP} /opt
RUN cd / \
    && cd /opt \
    && unzip -oq ${PORTAL_DISTRIBUTION_ZIP}  \
	&& ln -s ${PORTAL_DISTRIBUTION} portal 

RUN chmod -R 755  /opt/portal/


#Logs
RUN  rm -f /opt/portal/conf/logging.properties

#Properties
COPY portal.properties /home/portal
COPY cloud_fr.properties /home/portal

#send mail (remove tlsv1)
COPY java.security /usr/local/openjdk-8/jre/lib/security

# Entrypoint
COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["start"]

EXPOSE 8000
EXPOSE 8080
