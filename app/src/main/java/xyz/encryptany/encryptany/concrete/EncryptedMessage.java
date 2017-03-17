package xyz.encryptany.encryptany.concrete;

import java.util.Date;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by Max on 1/31/2017.
 */

public class EncryptedMessage implements Message {

    private String encryptedTxt;
    private String source;
    private String app;
    private Date date;

    public EncryptedMessage(String encryptedTxt, String source, String app, Date date) {
        this.encryptedTxt = encryptedTxt;
        this.source = source;
        this.app = app;
        this.date = date;
    }

    @Override
    public String getMessage() {
        return encryptedTxt;
    }

    @Override
    public String getAuthor() {
        return source;
    }

    @Override
    public String getApp() {
        return app;
    }

    @Override
    public Date getDate() {
        return date;
    }
}
