package xyz.encryptany.encryptany.listeners;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface AppListener {
    void setMessageReceived(String messageContent, String otherParticipant, String application, long unixDate, String uuid,String iv);
    // called usually when the user changes context away from the app (i.e., switches to a different app)
    void resetStatus();
    // called usually when the given app has a valid textEdit to send
    void readyForMessage();
    // called when waiting for the user to send the message
    void waitingForSend();
    // called once the message appears once again in the UI
    void messageSent();
}
