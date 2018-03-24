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
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@JBossLog
public class HttpSender implements EventPublisher {
    private final String eventUrl;
    private final String adminEventUrl;
    private final ObjectMapper mapper;

    /**
     * @param eventUrl
     * @param adminEventUrl
     */
    public HttpSender(String eventUrl, String adminEventUrl) {
        this.eventUrl = eventUrl;
        this.adminEventUrl = adminEventUrl;
        this.mapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEvent(Event event) {
        try {
            if (log.isDebugEnabled()) {
                log.debugf("Event: %s", mapper.writeValueAsString(event));
            }
            Response response = Request.Post(eventUrl)
                    .bodyString(mapper.writeValueAsString(event), APPLICATION_JSON).execute();
            return isSuccessResponse(response.returnResponse().getStatusLine());
        } catch (IOException e) {
            log.error("error sending event. Will retry", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEvent(AdminEvent adminEvent) {
        try {
            if (log.isDebugEnabled()) {
                log.debugf("AdminEvent: %s", mapper.writeValueAsString(adminEvent));
            }
            Response response = Request.Post(adminEventUrl)
                    .bodyString(mapper.writeValueAsString(adminEvent), APPLICATION_JSON).execute();
            return isSuccessResponse(response.returnResponse().getStatusLine());
        } catch (IOException e) {
            log.error("error sending admin event. Will retry", e);
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
