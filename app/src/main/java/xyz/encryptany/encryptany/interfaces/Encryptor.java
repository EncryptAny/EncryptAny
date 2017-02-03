package xyz.encryptany.encryptany.interfaces;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Encryptor {
    public void initialization();
    public void encryptMessage();
    public void decryptMessage();
    public void setKeys();
}
