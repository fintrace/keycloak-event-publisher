package org.fintrace.keycloak.events.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;
import org.fintrace.keycloak.events.EventPublisher;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@JBossLog
public class JMSSender implements EventPublisher {
    private final TopicConnectionFactory connectionFactory;
    private final Topic eventTopic;
    private final Topic adminEventTopic;
    private final ObjectMapper mapper;

    /**
     * @param connectionFactory JNDI connection factory
     * @param eventTopic        JNDI topic for event
     * @param adminEventTopic   JNDI topic for admin event
     */
    public JMSSender(String connectionFactory, String eventTopic, String adminEventTopic) {
        try {
            Context ctx = new InitialContext();
            this.connectionFactory = (TopicConnectionFactory) ctx.lookup(connectionFactory);
            this.eventTopic = (Topic) ctx.lookup(eventTopic);
            this.adminEventTopic = (Topic) ctx.lookup(adminEventTopic);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        this.mapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEvent(Event event) {
        try {
            return sendAndAcknowledge(mapper.writeValueAsString(event), eventTopic);
        } catch (JsonProcessingException e) {
            log.error("error writing JSON for event", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEvent(AdminEvent adminEvent) {
        try {
            return sendAndAcknowledge(mapper.writeValueAsString(adminEvent), adminEventTopic);
        } catch (JsonProcessingException e) {
            log.error("error writing JSON for admin event", e);
        }
        return false;
    }

    /**
     * @param payload
     * @param topic
     * @return
     */
    private boolean sendAndAcknowledge(String payload, Topic topic) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            final Message message = session.createMessage();
            message.setStringProperty("MEDIA_TYPE", APPLICATION_JSON.getMimeType());
            message.setStringProperty("BODY", payload);
            session.createProducer(topic).send(message);
            return true;
        } catch (JMSException e) {
            log.error("", e);
        }
        return false;
    }
}
