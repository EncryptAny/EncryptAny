package xyz.encryptany.encryptany.concrete;

import java.util.Date;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 1/26/2017.
 */

public abstract class MessageFactory {

    public MessageFactory(){

    }

    public Message createNewInitMessage(String source,String app){
        return new EncryptedMessage("LetsStartAnOTRConvo",source, app, new Date());
    }
    public Message createNewMessage(String message, String source, String app){
        return new EncryptedMessage(message, source, app, new Date());
    }


}
