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

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@JBossLog
public class EventPublisherProviderFactory implements EventListenerProviderFactory {

    private PublisherType type = PublisherType.HTTP;
    private EventsConsumer consumer;

    /**
     * {@inheritDoc}
     */
    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new EventPublisherProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Config.Scope scope) {
        log.debug("Setting up Event publisher provider factory");

        if (scope.get("type") != null) {
            this.type = PublisherType.valueOf(scope.get("type"));
        }

        if (this.type == PublisherType.HTTP) {
            this.consumer = new EventsConsumer(
                    new HttpSender(scope.get("eventUrl"), scope.get("adminEventUrl")));
        } else {
            this.consumer = new EventsConsumer(new JMSSender(
                    scope.get("jmsConnectionFactory"),
                    scope.get("jmsTopicEvent"),
                    scope.get("jmsTopicAdminEvent")
            ));
        }
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
