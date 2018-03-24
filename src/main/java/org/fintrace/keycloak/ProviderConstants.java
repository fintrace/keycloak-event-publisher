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

package org.fintrace.keycloak;

/**
 * @author <a href="mailto:koneru.chowdary@gmail.com">Venkaiah Chowdary Koneru</a>
 */
public final class ProviderConstants {

    public static final String PUBLISHER_TYPE = "type";
    public static final String JMS_CONNECTION_FACTORY = "jmsConnectionFactory";
    public static final String JMS_EVENT_TOPIC = "jmsTopicEvent";
    public static final String JMS_ADMIN_EVENT_TOPIC = "jmsTopicAdminEvent";
    public static final String URL_EVENT = "eventUrl";
    public static final String URL_ADMIN_EVENT = "adminEventUrl";
    public static final String PROVIDER_NAME = "event-publisher";
    public static final int EXECUTOR_DELAY_SECONDS = 90;

    /**
     * to prevent un-necessary instantiation
     */
    private ProviderConstants() {
    }
}
