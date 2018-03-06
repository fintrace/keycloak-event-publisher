package org.fintrace.keycloak.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Venkaiah Chowdary Koneru <koneru.chowdary@gmail.com>
 */
@AllArgsConstructor
@Data
public class KeycloakEvent<T> {
    private T event;
}
