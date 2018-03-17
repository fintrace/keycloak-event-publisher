package org.fintrace.keycloak.events;

import lombok.extern.jbosslog.JBossLog;
import org.fintrace.keycloak.events.http.HttpSender;
import org.fintrace.keycloak.events.jms.JMSSender;
import org.fintrace.queue.EventsConsumer;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
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

        if (scope.get("type") != null) {
            this.type = PublisherType.valueOf(scope.get("type"));
        }

        if (log.isTraceEnabled()) {
            log.tracef("Setting up %s type publisher", this.type.name());
        }

        if (this.type == PublisherType.JMS) {
            this.consumer = new EventsConsumer(new JMSSender(
                    scope.get("jmsConnectionFactory"),
                    scope.get("jmsTopicEvent"),
                    scope.get("jmsTopicAdminEvent")
            ));
        } else {
            this.consumer = new EventsConsumer(
                    new HttpSender(scope.get("eventUrl"), scope.get("adminEventUrl")));
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
        return "event-publisher";
    }
}
