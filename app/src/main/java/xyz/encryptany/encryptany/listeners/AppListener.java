package xyz.encryptany.encryptany.listeners;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface AppListener {
    void setMessageReceived(String messageContent, String otherParticipant, String application, long unixDate);
    void getMessages();
}
