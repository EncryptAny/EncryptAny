package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.EncryptionListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Encryptor {
    void initialization(Message message);
    void encryptMessage(Message message);
    void decryptMessage(Message message);
    void setEncryptionListener(EncryptionListener listener);
}
