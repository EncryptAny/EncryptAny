package xyz.encryptany.encryptany.listeners;

/**
 * Created by dakfu on 4/3/2017.
 */

// Callback to mediator when UI Sends a message
public interface UIListener {
    void sendMessageFromUIAdapter(String messageContent, String otherParticipant,String application);
}
