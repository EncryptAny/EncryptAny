package xyz.encryptany.encryptany;

import net.sqlcipher.Cursor;

import java.util.Date;

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

    long currentMessageUnixDate;

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

        currentMessageUnixDate = 0;
    }

    @Override
    public void setMessageReceived(String messageContent, String otherParticipant, String application, long unixDate) {
        // TODO implement
        currentMessageUnixDate = unixDate;
        receiveMessageFromApp(messageContent,otherParticipant,application);
    }

    @Override
    public void resetStatus() {
            // Prevent user from opening overlay
            uiAdapter.deactivate();
            // Minimize all overlay windows
            uiAdapter.setUIWindowState_Minimized();
    }

    @Override
    public void readyForMessage() {
        // If user closed EncryptAny
        // NOPE CHUCK TESTA
        if(uiAdapter.getUIWindowState() != UIAdapter.UIWindowState.CLOSED) {
            resetStatus();
        }
        uiAdapter.activate();
    }

    @Override
    public void waitingForSend() {
        // Freeze UI until it gets message sent
        uiAdapter.waitUntilReady();
        if(uiAdapter.getUIWindowState() != UIAdapter.UIWindowState.MINIMIZED)
            uiAdapter.setUIWindowState_Minimized();
    }

    @Override
    public void messageSent() {
        uiAdapter.ready();
    }

    @Override
    public Cursor getOldMessages(String app){
        return archiverAdapter.retrieveAppMessages(app);
    }

    public void sendMessageFromUIAdapter(String messageString,String otherParticipant, String appSource){
        // TODO Merge into app listener method
        //send message to encryption adapter and then to archiver and app adapter
        //generate message package to send to encryption adapter
        currentMessageUnixDate = new Date().getTime();
        Message payload = messageFactory.createNewMessage(messageString,otherParticipant,appSource,currentMessageUnixDate);
        encryptMessage(payload);
        archiveMessage(payload);
    }

    @Override
    public void startEncryptionProcess(String otherParticipant, String app) {
        encryptionAdapter.initialization(messageFactory.createNewInitMessage(otherParticipant,app));
    }

    private boolean encryptMessage(Message message){
        //send message to encryption adapter
        encryptionAdapter.encryptMessage(message);
        return false;
    }

    private void decryptMessage(Message message){
        encryptionAdapter.decryptMessage(message);
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
        Message payload = messageFactory.createNewMessage(result,otherParticipant,appSource,currentMessageUnixDate);
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
        Message message = messageFactory.createNewMessage(result, otherParticipant, appSource,currentMessageUnixDate);
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
        Message payload = messageFactory.createNewMessage(result,otherParticipant,appSource,currentMessageUnixDate);
        displayReceivedMessage(payload);
        archiveMessage(payload);
    }
}
