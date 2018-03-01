package org.fintrace.keycloak.events.http;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class HttpEventListenerProvider implements EventListenerProvider {
    private String eventsUrl;
    private String operationsUrl;

    public HttpEventListenerProvider(String eventsUrl, String operationsUrl) {
        this.eventsUrl = eventsUrl;
        this.operationsUrl = operationsUrl;
    }

    @Override
    public void onEvent(Event event) {
        queueEvent(event);
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        queueAdminEvent(event);
    }

    @Override
    public void close() {

    }

    private void queueEvent(Event event) {

    }

    private void queueAdminEvent(AdminEvent adminEvent) {

    }
}
