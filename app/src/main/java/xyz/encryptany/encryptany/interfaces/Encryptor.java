package xyz.encryptany.encryptany.interfaces;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Encryptor {
    public void initialization(Message message);
    public void encryptMessage(Message message);
    public void decryptMessage(Message message);
    public void setKeys();
}