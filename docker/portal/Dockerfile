FROM tomcat:8.5.60-jdk8
MAINTAINER Cédric Krommenhoek <ckrommenhoek@osivia.com>

RUN apt-get update && apt-get install -y locales vim  \
	&& localedef -i fr_FR -c -f UTF-8 -A /usr/share/locale/locale.alias fr_FR.UTF-8 \
	&& apt-get purge -y \
    && rm -rf /var/lib/apt/lists/*

ENV LANG fr_FR.utf8
RUN locale >> /etc/default/locale

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV TOMCAT_ROOT /usr/local/tomcat/webapps/

ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

ENV PORTAL_VERSION=5.0-SNAPSHOT
ENV PORTAL_DISTRIBUTION_NAME=index-cloud-ens-portal-distribution
ENV PORTAL_DISTRIBUTION ${PORTAL_DISTRIBUTION_NAME}-${PORTAL_VERSION}
ENV PORTAL_DISTRIBUTION_ZIP ${PORTAL_DISTRIBUTION_NAME}-${PORTAL_VERSION}.zip

RUN mkdir -p "$CATALINA_HOME"
RUN mkdir -p "/home/portal"
RUN mkdir -p "/data/exports"
WORKDIR $CATALINA_HOME

# Distribution
COPY ${PORTAL_DISTRIBUTION}.zip /usr/local/tomcat
RUN cd / \
    && cd /usr/local/tomcat \
    && unzip -oq ${PORTAL_DISTRIBUTION_ZIP}  \
    && rm -f ${PORTAL_DISTRIBUTION_ZIP}

#Logs
COPY logging.properties /usr/local/tomcat/conf
COPY catalina.properties /usr/local/tomcat/conf
COPY portal.properties /
COPY environnement_portail.properties /
COPY ldap-portlets.properties /
COPY cloud_fr.properties /home/portal
# Entrypoint
COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

EXPOSE 8000
EXPOSE 8080
