package xyz.encryptany.encryptany.listeners;

/**
 * Created by dakfu on 1/27/2017.
 */

public interface EncryptionListener {
    // sendEncryptedMessage is how the ecryption adapter sends encrypted messages and key exchanges via callbackd
    void sendEncryptedMessage(String msg, String otherParticipant, String appSource);
    void handshakeComplete();
    void messageDecrypted(String result,String otherParticipant, String appSource);
}
