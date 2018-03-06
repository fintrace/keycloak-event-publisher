package org.fintrace.keycloak.events.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.fintrace.keycloak.events.EventPublisher;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import java.io.IOException;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * @author Venkaiah Chowdary Koneru
 */
@JBossLog
public class HttpSender implements EventPublisher {
    private final String eventsUrl;
    private final String operationsUrl;
    private final ObjectMapper mapper;

    /**
     * @param eventsUrl
     * @param operationsUrl
     */
    public HttpSender(String eventsUrl, String operationsUrl) {
        this.eventsUrl = eventsUrl;
        this.operationsUrl = operationsUrl;
        this.mapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEvent(Event event) {
        try {
            Response response = Request.Post(eventsUrl)
                    .bodyString(mapper.writeValueAsString(event), APPLICATION_JSON).execute();
            return isSuccessResponse(response.returnResponse().getStatusLine());
        } catch (IOException e) {
            log.error("error sending event.will retry", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEvent(AdminEvent adminEvent) {
        try {
            Response response = Request.Post(operationsUrl)
                    .bodyString(mapper.writeValueAsString(adminEvent), APPLICATION_JSON).execute();
            return isSuccessResponse(response.returnResponse().getStatusLine());
        } catch (IOException e) {
            log.error("error sending admin event.will retry", e);
        }
        return false;
    }

    /**
     * validates whether the given StatusLine is a success code or failure.
     *
     * @param statusLine status line of http response
     * @return returns true if the status code is either 200/201/202 otherwise false
     */
    private boolean isSuccessResponse(StatusLine statusLine) {
        return statusLine.getStatusCode() == HttpStatus.SC_OK
                || statusLine.getStatusCode() == HttpStatus.SC_CREATED
                || statusLine.getStatusCode() == HttpStatus.SC_ACCEPTED;
    }
}
