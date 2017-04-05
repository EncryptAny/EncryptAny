package xyz.encryptany.encryptany;

import net.sqlcipher.Cursor;

import xyz.encryptany.encryptany.concrete.MessageFactory;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Encryptor;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.EncryptionListener;
import xyz.encryptany.encryptany.listeners.MessageSentListener;
import xyz.encryptany.encryptany.listeners.MessagesUpdatedListener;
import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public class Mediator implements MessagesUpdatedListener, EncryptionListener,UIListener {

    AppAdapter appAdapter;
    UIAdapter uiAdapter;
    Encryptor encryptionAdapter;
    Archiver archiverAdapter;
    MessageFactory messageFactory;

    boolean conversationReady;


    public Mediator(AppAdapter appAdapter, UIAdapter uiAdapter, Encryptor encryptionAdapter, Archiver archiverAdapter){
        this.appAdapter = appAdapter;
        this.uiAdapter = uiAdapter;
        this.encryptionAdapter = encryptionAdapter;
        this.archiverAdapter = archiverAdapter;
        messageFactory = new MessageFactory();

        conversationReady =false;
        this.encryptionAdapter.setEncryptionListener(this);
    }

    @Override
    public void setMessages() {

    }

    @Override
    public void getMessages() {

    }

    public Cursor getOldMessages(String app){
        return archiverAdapter.retrieveAppMessages(app);
    }
//    @Override
//    public void sendingMessage() {
//
//    }
    public void sendMessageFromUIAdapter(String messageString,String otherParticipant, String appSource){
        //send message to encryption adapter and then to archiver and app adapter
        //generate message package to send to encryption adapter

        Message payload = messageFactory.createNewMessage(messageString,otherParticipant,appSource);
        encryptMessage(payload);
        archiveMessage(payload);

    }

    private boolean encryptMessage(Message message){
        //send message to encryption adapter
        encryptionAdapter.encryptMessage(message);

        return false;
    }
    private boolean decryptMessage(Message message){

        encryptionAdapter.decryptMessage(message);
        return true;
    }

    private boolean archiveMessage(Message message){
        archiverAdapter.archiveMessage(message);

        return true;
    }

    private boolean displayReceivedMessage(Message message){
        //UI Adapter Call for displaying messages received from the app, depends on how Cory decides to implement his adapter

        return false;
    }

    private boolean displaySentMessage(){
        //Either Cory implements this inside his adapter and we get rid of this method or Cory does some sort of self UI adapter call
        return false;
    }

    private boolean receiveMessageFromApp(String result,String otherParticipant, String appSource){
        Message payload = messageFactory.createNewMessage(result,otherParticipant,appSource);
        encryptionAdapter.decryptMessage(payload);

        return false;
    }

    private boolean sendMessageToApp(Message message){
        //send the message package to the app adapter to deal with
        //also call displaySentMessage I guess?
        appAdapter.inputMessage(message);
        return false;
    }

    @Override
    public void sendingMessage(String result, String otherParticipant, String appSource) {
        //app adapter call with new message from the encrypted string
        Message message = messageFactory.createNewMessage(result, otherParticipant, appSource);
        sendMessageToApp(message);

    }

    @Override
    public void conversationReady() {
        //UI update to allow conversation to begin
        conversationReady = true;
    }
    @Override
    public void messageDecrypted(String result,String otherParticipant, String appSource){
        //display decrypted message to UI and store in archiver
        Message payload = messageFactory.createNewMessage(result,otherParticipant,appSource);
        displayReceivedMessage(payload);
        archiveMessage(payload);
    }
}
