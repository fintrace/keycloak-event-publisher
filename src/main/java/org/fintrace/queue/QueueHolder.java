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
