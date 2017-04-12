package xyz.encryptany.encryptany;

import net.sqlcipher.Cursor;

import xyz.encryptany.encryptany.concrete.MessageFactory;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Encryptor;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.EncryptionListener;
import xyz.encryptany.encryptany.listeners.AppListener;
import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public class Mediator implements AppListener, EncryptionListener, UIListener {

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
        appAdapter.setMessageUpdatedListener(this);
        uiAdapter.setUIListener(this);
        encryptionAdapter.setEncryptionListener(this);
    }

    @Override
    public void setMessageReceived(String messageContent, String otherParticipant, String application, long unixDate) {
        // TODO implement
    }

    @Override
    public void getMessages() {

    }
    
    @Override
    public Cursor getOldMessages(String app){
        return archiverAdapter.retrieveAppMessages(app);
    }

    public void sendMessageFromUIAdapter(String messageString,String otherParticipant, String appSource){
        //send message to encryption adapter and then to archiver and app adapter
        //generate message package to send to encryption adapter

        Message payload = messageFactory.createNewMessage(messageString,otherParticipant,appSource);
        encryptMessage(payload);
        archiveMessage(payload);

    }

    @Override
    public void startEncryptionProcess(String otherParticipant, String app) {
        encryptionAdapter.initialization(messageFactory.createNewMessage("Let's take this somewhere private",otherParticipant,app));
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
        uiAdapter.giveMessage(message);
        return true;
    }

    private boolean receiveMessageFromApp(String result,String otherParticipant, String appSource){
        Message payload = messageFactory.createNewMessage(result,otherParticipant,appSource);
        encryptionAdapter.decryptMessage(payload);

        return false;
    }

    private boolean sendMessageToApp(Message message){
        //send the message package to the app adapter to deal with
        //also call displaySentMessage I guess?
        appAdapter.sendMessage(message);
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
