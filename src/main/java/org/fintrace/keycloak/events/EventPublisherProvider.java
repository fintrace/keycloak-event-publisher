package org.fintrace.keycloak.events;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.queue.QueueHolder;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@JBossLog
public class EventPublisherProvider implements EventListenerProvider {

    public EventPublisherProvider() {
        log.debug("Setting up Event publisher provider");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(Event event) {
        queueEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        queueAdminEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * put keycloak event onto queue
     *
     * @param event
     */
    private void queueEvent(Event event) {
        try {
            QueueHolder.getQueue().put(new KeycloakEvent<>(event));
        } catch (InterruptedException e) {
            log.error("Interrupted while putting event", e);
        }
    }

    /**
     * put keycloak admin event onto queue
     *
     * @param adminEvent
     */
    private void queueAdminEvent(AdminEvent adminEvent) {
        try {
            QueueHolder.getQueue().put(new KeycloakEvent<>(adminEvent));
        } catch (InterruptedException e) {
            log.error("Interrupted while putting admin event", e);
        }
    }
}
