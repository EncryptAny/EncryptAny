package xyz.encryptany.encryptany.concrete;

import java.util.Date;

import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 1/26/2017.
 */

public class MessageFactory {

    public MessageFactory(){

    }

    public Message createNewInitMessage(String otherParticipant,String app){
        return new EncryptedMessage("LetsStartAnOTRConvo",otherParticipant, app, new Date());
    }
    public Message createNewMessage(String message, String otherParticipant, String app){
        return new EncryptedMessage(message, otherParticipant, app, new Date());
    }


}
