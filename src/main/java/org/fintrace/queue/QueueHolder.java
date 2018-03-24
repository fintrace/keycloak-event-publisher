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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.fintrace.keycloak.events.KeycloakEvent;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * In-memory blocking queue which will hold the events at first place and
 * then can be queried for processing.
 *
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueueHolder {

    private static final BlockingDeque<KeycloakEvent> LINKED_BLOCKING_DEQUE = new LinkedBlockingDeque<>();

    /**
     * returns the singleton LINKED_BLOCKING_DEQUE instance
     *
     * @return the singleton LINKED_BLOCKING_DEQUE instance
     */
    public static BlockingDeque<KeycloakEvent> getQueue() {
        return LINKED_BLOCKING_DEQUE;
    }
}
