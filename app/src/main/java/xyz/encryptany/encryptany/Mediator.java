package xyz.encryptany.encryptany;

import android.util.Log;

import net.sqlcipher.Cursor;

import java.util.Date;
import java.util.UUID;

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

    private static final String TAG = "Mediator";

    AppAdapter appAdapter;
    UIAdapter uiAdapter;
    Encryptor encryptionAdapter;
    Archiver archiverAdapter;
    MessageFactory messageFactory;

    private boolean conversationReady;
    private long uiMessageDateToForward;
    private String uiMessageUUIDToForward;
    private long msgRecievedDateToForward;
    private String msgRecievedUUIDToForward;

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
        uiMessageUUIDToForward = null;
        msgRecievedDateToForward = 0;
        msgRecievedUUIDToForward = null;
    }

    /* ============== BEGIN AppListener Methods ============== */

    @Override
    public void setMessageReceived(String messageContent, String otherParticipant, String application, long unixDate, String uuid) {
        if (archiverAdapter.doesMessageExist(uuid)) {
            return;
        } else {
            archiverAdapter.setMessageExists(uuid);
        }
        this.msgRecievedDateToForward = unixDate;
        this.msgRecievedUUIDToForward = uuid;
        Message payload = messageFactory.createNewMessage(messageContent, otherParticipant, application, uiMessageDateToForward, uuid);
        encryptionAdapter.decryptMessage(payload);
    }

    @Override
    public void resetStatus() {
        // Prevent user from opening overlay
        uiAdapter.disable();
        // Minimize all overlay windows
        uiAdapter.setUIWindowState_Minimized();
        uiMessageDateToForward = 0;
        uiMessageUUIDToForward = null;
        msgRecievedDateToForward = 0;
        conversationReady = false;
    }

    @Override
    public void readyForMessage() {
        // If user closed EncryptAny
        // NOPE CHUCK TESTA (aka, reopen the encryptany icon)
        showUIIfHidden();
        uiAdapter.enable();
    }

    @Override
    public void waitingForSend() {
        // Freeze UI until it gets message sent
        uiAdapter.waitForUserSend();
    }

    @Override
    public void messageSent() {
//        // TODO fix this bug
//        conversationReady = true;
        if (conversationReady) {
            // means just a normal message, go back to green
            uiAdapter.doneWaiting();
        } else {
            // means that we are probably (probz) still negotiating
            uiAdapter.waitForProcessing();
        }
    }

    /* ============== BEGIN UIListener Methods ============== */

    @Override
    public void sendMessageFromUIAdapter(String messageString, String otherParticipant, String appSource) {
        if (!conversationReady) {
            startEncryptionProcess(otherParticipant, appSource);
            return;
        }
        // Set UI to WAIT until message encryption is done
        uiAdapter.waitForProcessing();
        //send message to encryption adapter and then to archiver and app adapter
        //generate message package to send to encryption adapter
        Message payload = messageFactory.createNewMessage(messageString, otherParticipant, appSource);
        this.uiMessageDateToForward = payload.getDate();
        this.uiMessageUUIDToForward = payload.uuid();
        archiveMessage(payload);
        encryptionAdapter.encryptMessage(payload);
    }

    @Override
    public void startEncryptionProcess(String otherParticipant, String app) {
        uiAdapter.waitForProcessing();
        Message msg = messageFactory.createNewInitMessage(otherParticipant, app);
        this.uiMessageDateToForward = msg.getDate();
        archiveMessage(msg);
        encryptionAdapter.initialization(msg);
    }

    @Override
    public Cursor getOldMessages(String app) {
        // TODO see if this actually matters in anything and if so do something but if not oh well
        return archiverAdapter.retrieveAppMessages(app);
    }

    /* ============== BEGIN EncryptionListener Methods ============== */

    @Override
    public void sendEncryptedMessage(String result, String otherParticipant, String appSource) {
        uiAdapter.waitForUserSend();
        long dateToUse = this.uiMessageDateToForward;
        if (dateToUse == 0) {
            dateToUse = getNow();
        } else {
            this.uiMessageDateToForward = 0;
        }
        String uuid = this.uiMessageUUIDToForward;
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        } else {
            uuid = null;
        }
        //app adapter call with new message from the encrypted string
        Message message = messageFactory.createNewMessage(result, otherParticipant, appSource, dateToUse, uuid);
        archiverAdapter.setMessageExists(message.uuid());
        appAdapter.sendMessage(message);
    }

    @Override
    public void handshakeComplete() {
        //UI update to allow conversation to begin
        conversationReady = true;
        uiAdapter.doneWaiting();
    }

    @Override
    public void messageDecrypted(String result, String otherParticipant, String appSource) {
        //display decrypted message to UI and store in archiver
        if (this.msgRecievedDateToForward == 0) {
            throw new IllegalStateException("message is zero on decrypt, which in theory is impossible");
        } else {
            this.msgRecievedDateToForward = 0;
        }
        if (this.msgRecievedUUIDToForward == null) {
            throw new IllegalStateException("uuid is null on decrypt, which in theory is impossible");
        } else {
            this.msgRecievedUUIDToForward = null;
        }
        Message payload = messageFactory.createNewMessage(result, otherParticipant, appSource, msgRecievedDateToForward, msgRecievedUUIDToForward);
        uiAdapter.giveMessage(payload);
        archiveMessage(payload);
    }

    /* ============== BEGIN Private Helper Methods ============== */

    private void showUIIfHidden() {
        if (uiAdapter.getUIWindowState() != UIAdapter.UIWindowState.MINIMIZED) {
            uiAdapter.setUIWindowState_Minimized();
        }
    }

    private static long getNow() {
        long time = (new Date()).getTime();
        return time;
    }


    private void decryptMessage(Message message) {
        encryptionAdapter.decryptMessage(message);
    }

    private void archiveMessage(Message message) {
        archiverAdapter.archiveMessage(message);
    }
}
