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

    long uiMessageDateToForward;

    public Mediator(AppAdapter appAdapter, UIAdapter uiAdapter, Encryptor encryptionAdapter, Archiver archiverAdapter) {
        this.appAdapter = appAdapter;
        this.uiAdapter = uiAdapter;
        this.encryptionAdapter = encryptionAdapter;
        this.archiverAdapter = archiverAdapter;
        messageFactory = new MessageFactory();

        conversationReady = false;
        this.encryptionAdapter.setEncryptionListener(this);
        appAdapter.setMessageUpdatedListener(this);
        uiAdapter.setUIListener(this);
        encryptionAdapter.setEncryptionListener(this);

        uiMessageDateToForward = 0;
    }

    /* ============== BEGIN AppListener Methods ============== */

    @Override
    public void setMessageReceived(String messageContent, String otherParticipant, String application, long unixDate) {
        // TODO implement
        this.uiMessageDateToForward = unixDate;
        Message payload = messageFactory.createNewMessage(messageContent, otherParticipant, application, uiMessageDateToForward);
        encryptionAdapter.decryptMessage(payload);
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
        // TODO Implement
        // If user closed EncryptAny
        // NOPE CHUCK TESTA
        if (uiAdapter.getUIWindowState() == UIAdapter.UIWindowState.CLOSED) {
            uiAdapter.setUIWindowState_Minimized();
        }
        uiAdapter.activate();
    }

    @Override
    public void waitingForSend() {
        // TODO Implement
        // Freeze UI until it gets message sent
        uiAdapter.waitUntilReady();
        if(uiAdapter.getUIWindowState() != UIAdapter.UIWindowState.MINIMIZED) {
            uiAdapter.setUIWindowState_Minimized();
        }
    }

    @Override
    public void messageSent() {
        // TODO Implement
        uiAdapter.ready();
    }

    /* ============== BEGIN UIListener Methods ============== */

    @Override
    public void sendMessageFromUIAdapter(String messageString, String otherParticipant, String appSource) {
        //send message to encryption adapter and then to archiver and app adapter
        //generate message package to send to encryption adapter
        this.uiMessageDateToForward = (new Date()).getTime();
        Message payload = messageFactory.createNewMessage(messageString, otherParticipant, appSource, uiMessageDateToForward);
        // TODO set ui to WAIT
        encryptionAdapter.encryptMessage(payload);
        archiveMessage(payload);
    }

    @Override
    public void startEncryptionProcess(String otherParticipant, String app) {
        encryptionAdapter.initialization(messageFactory.createNewInitMessage(otherParticipant, app));
        // TODO Set UI to WAIT
    }

    @Override
    public Cursor getOldMessages(String app) {
        // TODO see if this actually matters in anything and if so do something but if not oh well
        //return archiverAdapter.retrieveAppMessages(app);
        return null;
    }

    /* ============== BEGIN EncryptionListener Methods ============== */

    @Override
    public void sendEncryptedMessage(String result, String otherParticipant, String appSource) {
        //app adapter call with new message from the encrypted string
        Message message = messageFactory.createNewMessage(result, otherParticipant, appSource, uiMessageDateToForward);
        // TODO Update UI to Reflect Waiting for User To Send
        appAdapter.sendMessage(message);
    }

    @Override
    public void handshakeComplete() {
        // TODO Implement
        uiAdapter.activate();
        //UI update to allow conversation to begin
        conversationReady = true;
    }

    @Override
    public void messageDecrypted(String result, String otherParticipant, String appSource) {
        // TODO Implement
        //display decrypted message to UI and store in archiver
        Message payload = messageFactory.createNewMessage(result, otherParticipant, appSource, uiMessageDateToForward);
        displayReceivedMessage(payload);
        archiveMessage(payload);
    }

    /* ============== BEGIN Private Helper Methods ============== */



    private void decryptMessage(Message message) {
        encryptionAdapter.decryptMessage(message);
    }

    private boolean archiveMessage(Message message) {
        archiverAdapter.archiveMessage(message);

        return true;
    }

    private boolean displayReceivedMessage(Message message) {
        //UI Adapter Call for displaying messages received from the app, depends on how Cory decides to implement his adapter
        uiAdapter.giveMessage(message);
        return true;
    }
}
