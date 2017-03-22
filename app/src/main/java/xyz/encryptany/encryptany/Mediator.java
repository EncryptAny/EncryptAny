package xyz.encryptany.encryptany;

import xyz.encryptany.encryptany.concrete.MessageFactory;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Encryptor;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.EncryptionListener;
import xyz.encryptany.encryptany.listeners.MessageSentListener;
import xyz.encryptany.encryptany.listeners.MessagesUpdatedListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public class Mediator implements MessagesUpdatedListener, EncryptionListener {

    AppAdapter appAdapter;
    UIAdapter uiAdapter;
    Encryptor encryptionAdapter;
    Archiver archiverAdapter;
    MessageFactory messageFactory;


    public Mediator(AppAdapter appAdapter, UIAdapter uiAdapter, Encryptor encryptionAdapter, Archiver archiverAdapter){
        this.appAdapter = appAdapter;
        this.uiAdapter = uiAdapter;
        this.encryptionAdapter = encryptionAdapter;
        this.archiverAdapter = archiverAdapter;
        messageFactory = new MessageFactory();

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
    public boolean sendMessage(String messageString,String otherParticipant, String appSource){
        //send message to encryption adapter and then to archiver and app adapter
        //generate message package to send to encryption adapter
        Message payload = messageFactory.createNewMessage(messageString,otherParticipant,appSource);
        encryptMessage(payload);
        archiveMessage(payload);
        return false;

    }

    private boolean encryptMessage(Message message){
        //send message to encryption adapter
        encryptionAdapter.encryptMessage(message);

        return false;
    }

    private boolean archiveMessage(Message message){
        archiverAdapter.archiveMessage(message);

        return true;
    }

    private boolean displayReceivedMessage(Message message){
        return false;
    }

    private boolean displaySentMessage(){
        return false;
    }

    private boolean receiveMessageFromApp(){
        return false;
    }

    private boolean sendMessageToApp(Message message){
        //send the message package to the app adapter to deal with
        //also call displaySentMessage I guess?
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
    }
    @Override
    public void messageDecrypted(String result,String otherParticipant, String appSource){
        //display decrypted message to UI and store in archiver
        Message payload = messageFactory.createNewMessage(result,otherParticipant,appSource);
        displayReceivedMessage(payload);
        archiveMessage(payload);
    }
}
