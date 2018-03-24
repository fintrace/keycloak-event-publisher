/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.fintrace.keycloak.events;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.keycloak.events.http.HttpSender;
import org.fintrace.keycloak.events.jms.JMSSender;
import org.fintrace.queue.EventsConsumer;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import static org.fintrace.keycloak.ProviderConstants.*;

/**
 * @author <a href="mailto:koneru.chowdary@gmail.com">Venkaiah Chowdary Koneru</a>
 */
@JBossLog
public class EventPublisherProviderFactory implements EventListenerProviderFactory {

    private PublisherType type = PublisherType.HTTP;
    private EventsConsumer consumer;
    private EventPublisherProvider eventPublisher;

    /**
     * {@inheritDoc}
     */
    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return eventPublisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Config.Scope scope) {
        log.info("Setting up Event publisher provider factory");

        if (scope.get(PUBLISHER_TYPE) != null) {
            this.type = PublisherType.valueOf(scope.get(PUBLISHER_TYPE));
        }

        if (log.isTraceEnabled()) {
            log.tracef("Setting up %s type publisher", this.type.name());
        }

        if (this.type == PublisherType.JMS) {
            this.consumer = new EventsConsumer(new JMSSender(
                    scope.get(JMS_CONNECTION_FACTORY),
                    scope.get(JMS_EVENT_TOPIC),
                    scope.get(JMS_ADMIN_EVENT_TOPIC)
            ));
        } else {
            this.consumer = new EventsConsumer(
                    new HttpSender(scope.get(URL_EVENT), scope.get(URL_ADMIN_EVENT)));
        }

        this.eventPublisher = new EventPublisherProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        this.consumer.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.consumer.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
}
