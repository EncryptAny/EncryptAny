package xyz.encryptany.encryptany.testing;

import xyz.encryptany.encryptany.interfaces.Encryptor;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.listeners.EncryptionListener;

/**
 * Created by max on 3/24/17.
 */

public class NoOpEncryptor implements Encryptor {

    EncryptionListener el = null;

    @Override
    public void initialization(Message message) {

    }

    @Override
    public void encryptMessage(Message message) {
        el.sendingMessage(message.getMessage(), message.getAuthor(), message.getApp());
    }

    @Override
    public void decryptMessage(Message message) {
        el.messageDecrypted(message.getMessage(), message.getAuthor(), message.getApp());
    }

    @Override
    public void setKeys() {

    }

    @Override
    public void setEncryptionListener(EncryptionListener e) {
        this.el = e;
    }
}
