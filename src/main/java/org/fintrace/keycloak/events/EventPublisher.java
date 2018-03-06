package org.fintrace.keycloak.events;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

/**
 * @author Venkaiah Chowdary Koneru
 */
public interface EventPublisher {

    /**
     * @return
     */
    boolean sendEvent(Event event);

    /**
     * @return
     */
    boolean sendEvent(AdminEvent adminEvent);
}
