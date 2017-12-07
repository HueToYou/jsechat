package org.huetoyou.jsechat;

/**
 * Connection to the Stack Exchange server
 */
public class Connection {

    /**
     * Listener for connection events
     */
    public interface Listener {

        /**
         * Indicate that authentication completed
         */
        void onAuthenticated();

        /**
         * Indicate that an event has occurred
         * @param event information about the event
         */
        void onEvent(Event event);
    }
}
