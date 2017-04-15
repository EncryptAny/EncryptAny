package xyz.encryptany.encryptany.concrete;

import java.util.Date;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 3/20/2017.
 */

public class UnencryptedMessage implements Message {
    private String unencryptedTxt;
    private String author;
    private String app;
    private long date;
    private String uuid;

    public UnencryptedMessage(String unencryptedTxt, String author, String app, long date) {
        this.unencryptedTxt = unencryptedTxt;
        this.author = author;
        this.app = app;
        this.date = date;
    }

    @Override
    public String getMessage() {
        return unencryptedTxt;
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
