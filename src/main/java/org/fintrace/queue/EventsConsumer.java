package org.fintrace.queue;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.keycloak.events.EventPublisher;
import org.fintrace.keycloak.events.KeycloakEvent;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * consumer of in-memory queue. based on the EventPublisher type,
 * further processing of the events will be done.
 *
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
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

    }

    /**
     * This shall be invoked once the providers are discovered.
     */
    public void init() {
        executorService.schedule(this::handleEvent, 90, TimeUnit.SECONDS);
    }

    /**
     * This shall be invoked before completing the server shutdown. This is needed
     * in order to gracefully shutdown the processing
     */
    public void shutdown() {
        isShutdownRequested = true;
        executorService.shutdown();
        if (log.isDebugEnabled()) {
            log.debug("Shutdown has been requested. Will shutdown once the processing has been completed");
        }

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
