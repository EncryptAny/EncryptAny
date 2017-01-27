package xyz.encryptany.encryptany;

import xyz.encryptany.encryptany.Listeners.MessageSendingListener;
import xyz.encryptany.encryptany.Listeners.MessagesUpdatedListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public class Mediator implements MessagesUpdatedListener,MessageSendingListener{

    AppAdapterInterface appAdapter;
    UIAdapterInterface uiAdapter;
    EncryptionInterface encryptionAdapter;
    ArchiverInterface archiverAdapter;


    public Mediator(AppAdapterInterface appAdapter, UIAdapterInterface uiAdapter, EncryptionInterface encryptionAdapter, ArchiverInterface archiverAdapter){
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
    @Override
    public void sendingMessage() {

    }
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



}
