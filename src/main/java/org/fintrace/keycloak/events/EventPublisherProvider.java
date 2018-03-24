/*
 *  (C) Copyright 2018 fintrace (https://fintrace.org/) and others.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.fintrace.keycloak.events;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.queue.QueueHolder;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:koneru.chowdary@gmail.com">Venkaiah Chowdary Koneru</a>
 */
@JBossLog
public class EventPublisherProvider implements EventListenerProvider {
    private static final Set<EventType> INCLUDED_EVENTS = new HashSet<>();
    private static final Set<ResourceType> INCLUDED_ADMIN_EVENTS = new HashSet<>();

    static {
        INCLUDED_EVENTS.add(EventType.CLIENT_UPDATE);
        INCLUDED_EVENTS.add(EventType.REGISTER);
        INCLUDED_EVENTS.add(EventType.CLIENT_REGISTER);
        INCLUDED_EVENTS.add(EventType.CLIENT_DELETE);

        INCLUDED_ADMIN_EVENTS.add(ResourceType.GROUP);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.GROUP_MEMBERSHIP);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.REALM);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.REALM_ROLE);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.REALM_ROLE_MAPPING);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.USER);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.CLIENT);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.CLIENT_ROLE);
        INCLUDED_ADMIN_EVENTS.add(ResourceType.CLIENT_ROLE_MAPPING);
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
