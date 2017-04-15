package xyz.encryptany.encryptany.concrete;

import java.util.Date;
import java.util.UUID;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by Max on 1/31/2017.
 */

public class EncryptedMessage implements Message {

    private String encryptedTxt;
    private String author;
    private String app;
    private long date;
    private String uuid;

    public EncryptedMessage(String encryptedTxt, String author, String app, long date, String uuid) {
        this.encryptedTxt = encryptedTxt;
        this.author = author;
        this.app = app;
        this.date = date;
        this.uuid = uuid;
    }

    @Override
    public String getMessage() {
        return encryptedTxt;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getApp() {
        return app;
    }

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public String uuid() {
        return this.uuid;
    }
}
