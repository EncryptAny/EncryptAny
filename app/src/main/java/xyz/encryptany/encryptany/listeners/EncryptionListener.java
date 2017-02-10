package xyz.encryptany.encryptany.listeners;

/**
 * Created by dakfu on 1/27/2017.
 */

public interface EncryptionListener {
    public void sendingMessage(String result);
    public void conversationReady();
    public void messageDecrypted(String result);

}
