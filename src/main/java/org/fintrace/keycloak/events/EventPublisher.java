package org.fintrace.keycloak.events;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

/**
 * @author Venkaiah Chowdary Koneru
 */
public interface EventPublisher {

    /**
     * publishes event
     *
     * @return true if operation is successful otherwise false
     */
    boolean sendEvent(Event event);

    /**
     * publishes admin event
     *
     * @return true if operation is successful otherwise false
     */
    boolean sendEvent(AdminEvent adminEvent);
}
