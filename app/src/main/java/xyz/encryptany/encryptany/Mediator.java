package xyz.encryptany.encryptany;

import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Encryptor;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.MessageSentListener;
import xyz.encryptany.encryptany.listeners.MessagesUpdatedListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public class Mediator implements MessagesUpdatedListener, MessageSentListener {

    AppAdapter appAdapter;
    UIAdapter uiAdapter;
    Encryptor encryptionAdapter;
    Archiver archiverAdapter;


    public Mediator(AppAdapter appAdapter, UIAdapter uiAdapter, Encryptor encryptionAdapter, Archiver archiverAdapter){
        this.appAdapter = appAdapter;
        this.uiAdapter = uiAdapter;
        this.encryptionAdapter = encryptionAdapter;
        this.archiverAdapter = archiverAdapter;

    }

    @Override
    public void setMessages() {

    }
    @Override
    public void getMessages(){
        
    }
//    @Override
//    public void sendingMessage() {
//
//    }
    public boolean sendMessage(){
        return false;

    }

    private boolean encryptMessage(){


        return false;
    }

    private boolean archiveMessage(){
        return false;
    }

    private boolean displayReceivedMessage(){
        return false;
    }

    private boolean displaySentMessage(){
        return false;
    }

    private boolean receiveMessageFromApp(){
        return false;
    }

    private boolean sendMessageToApp(){
        return false;
    }


    @Override
    public void sendMessage(Message msgSent) {

    }
}
