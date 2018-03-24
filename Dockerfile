FROM jboss/keycloak:4.0.0.Beta1

ADD target/keycloak-event-publisher-jar-with-dependencies.jar /opt/jboss/keycloak/providers/

ADD docker/standalone.xml /opt/jboss/keycloak/standalone/configuration/

EXPOSE 8080 9990
ENTRYPOINT [ "/opt/jboss/docker-entrypoint.sh" ]

CMD ["-b", "0.0.0.0"]