package org.fintrace.keycloak.events.http;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
public class HttpEventListenerProviderFactory implements EventListenerProviderFactory {

    private String eventsUrl;
    private String operationsUrl;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return null;
    }

    @Override
    public void init(Config.Scope scope) {
        this.eventsUrl = scope.get("eventsUrl");
        this.operationsUrl = scope.get("operationsUrl");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "http-event-publisher";
    }
}
