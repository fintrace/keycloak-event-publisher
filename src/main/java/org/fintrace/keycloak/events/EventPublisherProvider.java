package org.fintrace.keycloak.events;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.queue.QueueHolder;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@JBossLog
public class EventPublisherProvider implements EventListenerProvider {
    private static final Set<EventType> INCLUDED_EVENTS = new HashSet<>();
    private static final Set<ResourceType> INCLUDED_ADMIN_EVENTS = new HashSet<>();

    static {
        INCLUDED_EVENTS.add(EventType.CLIENT_UPDATE);
        INCLUDED_EVENTS.add(EventType.CLIENT_REGISTER);
        INCLUDED_EVENTS.add(EventType.CLIENT_DELETE);

        INCLUDED_ADMIN_EVENTS.add(ResourceType.GROUP);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.GROUP_MEMBERSHIP);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.REALM);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.REALM_ROLE);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.REALM_ROLE_MAPPING);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.USER);
    }

    public EventPublisherProvider() {
        log.info("Setting up Event publisher provider");
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
        queueAdminEvent(event, includeRepresentation);
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
            if (INCLUDED_EVENTS.contains(event.getType())) {
                QueueHolder.getQueue().put(new KeycloakEvent<>(event));
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while putting event", e);
        }
    }

    /**
     * put keycloak admin event onto queue
     *
     * @param adminEvent
     * @param includeRepresentation
     */
    private void queueAdminEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        try {
            if (INCLUDED_ADMIN_EVENTS.contains(adminEvent.getResourceType())) {
                QueueHolder.getQueue().put(new KeycloakEvent<>(adminEvent));
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while putting admin event", e);
        }
    }
}
