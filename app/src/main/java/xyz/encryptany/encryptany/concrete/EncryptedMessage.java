package xyz.encryptany.encryptany.concrete;

import java.util.Date;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by Max on 1/31/2017.
 */

public class EncryptedMessage implements Message {

    private String encryptedTxt;
    private String sender;
    private String hash;
    private long unixTS;

    public EncryptedMessage(String encryptedTxt, String sender, String hash, long unixTS) {
        this.encryptedTxt = encryptedTxt;
        this.sender = sender;
        this.hash = hash;
        this.unixTS = unixTS;
    }

    @Override
    public String getMessage() {
        return encryptedTxt;
    }

    @Override
    public String getSender() {
        return null;
    }

    @Override
    public Date getDate() {
        return null;
    }
}
