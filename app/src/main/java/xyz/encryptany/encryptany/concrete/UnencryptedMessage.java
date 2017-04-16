package xyz.encryptany.encryptany.concrete;

import java.util.Date;
import java.util.UUID;

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

    public UnencryptedMessage(String unencryptedTxt, String author, String app, String uuid) {
        this.unencryptedTxt = unencryptedTxt;
        this.author = author;
        this.app = app;
        this.date = (new Date()).getTime();
        this.uuid = uuid;
    }

    public UnencryptedMessage(String unencryptedTxt, String author, String app) {
        this.unencryptedTxt = unencryptedTxt;
        this.author = author;
        this.app = app;
        this.date = (new Date()).getTime();
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public String getMessage() {
        return unencryptedTxt;
    }

    @Override
    public String getOtherParticpant() {
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

    @Override
    public String getIV() {
        return null;
    }
}
