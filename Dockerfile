FROM jboss/keycloak:4.0.0.Beta1

ADD target/keycloak-event-publisher-1.0.0-SNAPSHOT.jar /opt/jboss/keycloak/providers/

ADD docker/standalone.xml /opt/jboss/keycloak/standalone/configuration/

EXPOSE 8080 9990
ENTRYPOINT [ "/opt/jboss/docker-entrypoint.sh" ]

CMD ["-b", "0.0.0.0"]