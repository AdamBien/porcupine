package com.airhacks.threading.messages.control;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author airhacks.com
 */
public class MessageReceiver {

    public String receiveLightMessage() {
        return "Light Message " + Instant.now();
    }

    public String receiveHeavyMessage() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Heavy Message " + Instant.now();
    }
}
