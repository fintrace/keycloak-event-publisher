FROM jboss/keycloak:3.4.3.Final

ADD keycloak-http-event-publisher-jar-with-dependencies.jar /opt/jboss/keycloak/providers/

ADD standalone.xml /opt/jboss/keycloak/standalone/configuration/

EXPOSE 8080 9990
ENTRYPOINT [ "/opt/jboss/docker-entrypoint.sh" ]

CMD ["-b", "0.0.0.0"]