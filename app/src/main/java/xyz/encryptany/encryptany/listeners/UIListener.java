package xyz.encryptany.encryptany.listeners;


import net.sqlcipher.Cursor;

/**
 * Created by dakfu on 4/3/2017.
 */

// Callback to mediator when UI Sends a message
public interface UIListener {
    void sendMessageFromUIAdapter(String messageContent, String otherParticipant,String application);
    void startEncryptionProcess(String otherParticipant, String app);
    Cursor getOldMessages(String app);
}
