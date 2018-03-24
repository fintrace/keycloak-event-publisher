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

package org.fintrace.queue;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.keycloak.events.EventPublisher;
import org.fintrace.keycloak.events.KeycloakEvent;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.fintrace.keycloak.ProviderConstants.EXECUTOR_DELAY_SECONDS;

/**
 * consumer of in-memory queue. based on the EventPublisher type,
 * further processing of the events will be done.
 *
 * @author <a href="mailto:koneru.chowdary@gmail.com">Venkaiah Chowdary Koneru</a>
 */
@JBossLog
public class EventsConsumer {
    private final ScheduledExecutorService executorService;
    private final EventPublisher publisher;
    private boolean isShutdownRequested = false;

    /**
     * @param publisher
     */
    public EventsConsumer(EventPublisher publisher) {
        this.publisher = publisher;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        log.info("Initialized a simple events consumer to consume keycloak events from queue");

    }

    /**
     * This shall be invoked once the providers are discovered.
     */
    public void init() {
        executorService.schedule(this::handleEvent, EXECUTOR_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * This shall be invoked before completing the server shutdown. This is needed
     * in order to gracefully shutdown the processing
     */
    public void shutdown() {
        isShutdownRequested = true;
        executorService.shutdown();

        log.info("Shutdown has been requested. Will shutdown once the processing has been completed");


        while (!executorService.isTerminated()) {
            try {
                log.info("Still processing the queue. will check again in a moment");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Consumer interrupted", e);
            }
        }
    }

    /**
     * peek from the queue and post the data to remote HTTP endpoint.
     * This will remove from the queue only if the response is success.
     * Otherwise this will retry indefinitely until a shutdown signal is received.
     */
    private void handleEvent() {
        while (!isShutdownRequested) {
            if (log.isTraceEnabled()) {
                log.trace("inside handle event");
            }
            KeycloakEvent event = QueueHolder.getQueue().peek();
            if (event != null) {
                if (log.isTraceEnabled()) {
                    log.tracef("received %s", event.getEvent());
                }
                boolean isAdminEvent = event.getEvent() instanceof AdminEvent;
                if (isAdminEvent) {
                    if (!publisher.sendEvent((AdminEvent) event.getEvent())) {
                        continue;
                    }
                } else if (!publisher.sendEvent((Event) event.getEvent())) {
                    continue;
                }
                QueueHolder.getQueue().remove();
            }
        }
    }
}
