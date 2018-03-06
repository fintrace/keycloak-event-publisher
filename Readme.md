# keycloak-event-publisher

### Features
* Multiple Publisher types
    * HTTP
        * Http sender for Keycloak events 
            * Ability to supply events that can be excluded (TODO)
        * Http sender for Keycloak Admin operations 
            * Ability to supply events that can be excluded (TODO)
    * JMS
* Queue events before publishing
* Retry in case of failure
    * Ability to supply retry strategies (TODO)
* Serialize queue events to a file or database (TODO)

## Build

### Build Docker Image