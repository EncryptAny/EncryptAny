package xyz.encryptany.encryptany.concrete;

import ca.uwaterloo.crysp.otr.UserState;
import ca.uwaterloo.crysp.otr.iface.OTRCallbacks;
import ca.uwaterloo.crysp.otr.iface.OTRContext;
import ca.uwaterloo.crysp.otr.iface.OTRInterface;
import xyz.encryptany.encryptany.interfaces.Encryptor;
import ca.uwaterloo.crysp.otr.*;

import ca.uwaterloo.crysp.otr.iface.*;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.listeners.EncryptionListener;

/**
 * Created by dakfu on 2/3/2017.
 */

public class OTREncryptor implements Encryptor {
    //holds the hashtable for the session keys for each conversation
    //This does most of the actual work for the OTR encryption, at least where most of the base functions are called
    OTRInterface conversation;
    //Callbacks the OTR framework uses and calls at the relevant steps in its functions
    OTRCallbacks callback;
    //Used for the Socialist Millionare's Protocol initialization but we're aren't worrying about that for now
    OTRContext context;
    //Listener for the mediator to send out the strings from the encryptor. (Kind of like a visitor but not really :()
    EncryptionListener encryptionListener;

    public OTREncryptor() {


        //The JCA provider is just the particular setting this version of OTR uses as suggested in the README and Example program
        conversation = new UserState(new ca.uwaterloo.crysp.otr.crypt.jca.JCAProvider());
    }

    //How we're handling the initialization with the OTR Framework:
    //Send a default string our app will pick up on to trigger the initialization on the receiving end
    //Have the overlay obscure the messaging app and have some loading animation going on while our app
    //starts sending messages over the messaging client to preform the DiffieHelman Exchange for generating
    //the symmetric keys. After the initial string is sent to a new subject the OTR framework is primed to receive
    //a new addition to its conversation keys hashtable and upon the next received message will return a
    //corresponding DH exchange message. Upon completeion we will call the mediator's listener signalling
    //that the conversation is doneWaiting
    @Override
    public void initialization(Message message) {

        //The message in the case will be loaded with our default string sent from the UI Adapter
        //The UI adapter will send the default string to the mediator and the mediator will construct the message
        //with the appropriate target recipient as well as what app the message is going through.
        String recipient = message.getAuthor();
        String messageContent = message.getMessage();

        //The app isn't necessarilly the required string for this function, a secondary string to uniquely identify the
        //conversation is the purpose of this field. For the sake of relevance we're naming it after each app the conversation
        //is on
        String app = message.getApp();


        try {
            //To be honest not sure what the purpose of these lines are
            //OTRT(type)L(length)V(value) as described in the framework files.
            //The readme says the tlvs can be null most of the time but I left these lines because of the example.
            OTRTLV[] tlvs = new OTRTLV[1];
            tlvs[0] = new TLV(9, "TestTLV".getBytes());

            //(accountname[in this case just labeling it me as each app will be used by one user],protocol[the app we're using]
            //,recipient,message,tlvs,fragment policy [How we're splitting up the message], callback [ what the framework uses to send the messages among
            //other things])
            conversation.messageSending("me", app,
                    recipient, messageContent, tlvs, Policy.FRAGMENT_SEND_ALL, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    public void encryptMessage(Message message) {
        String recipient = message.getAuthor();
        String messageContent = message.getMessage();
        String app = message.getApp();
        try {
            OTRTLV[] tlvs = new OTRTLV[1];
            tlvs[0] = new TLV(9, "TestTLV".getBytes());
            conversation.messageSending("me", app,
                    recipient, messageContent, tlvs, Policy.FRAGMENT_SEND_ALL, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void decryptMessage(Message message) {
        String app = message.getApp();
        String sender = message.getAuthor();
        String messageContent = message.getMessage();
        try {

            StringTLV stlv = conversation.messageReceiving("me", app, sender, messageContent, callback);
            if (stlv != null) {
                messageContent = stlv.msg;
                encryptionListener.messageDecrypted(messageContent, sender, app);
                //System.out.println("\033[31mFrom OTR:"+res.length()+":\033[0m"+res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void setKeys() {
        //otr sets the keys, not overriding interface here
    }

    @Override
    public void setEncryptionListener(EncryptionListener listener) {
        this.encryptionListener = listener;
        //The local callback is implemented within this file
        callback = new LocalCallback(encryptionListener);
    }
}

class LocalCallback implements OTRCallbacks {

    private EncryptionListener encryptionListener;

    public LocalCallback(EncryptionListener encryptionListener) {
        this.encryptionListener = encryptionListener;
    }

    //THIS IS WHERE THE FRAMWORK SENDS MESSAGES OUT
    public void injectMessage(String accName, String prot, String rec, String msg) {
        if (msg == null) return;
        System.out.println("\033[31mInjecting message to the recipient:"
                + msg.length() + ":\033[35m" + msg + "\033[0m");
        //out.println(msg);
        // out.flush();
        encryptionListener.sendEncryptedMessage(msg, rec, prot);
    }

    public int getOtrPolicy(OTRContext conn) {
        return Policy.DEFAULT;
    }

    @Override
    public void goneSecure(OTRContext context) {
        System.out.println("\033[31mAKE succeeded\033[0m");
        encryptionListener.handshakeComplete();
    }

    public int isLoggedIn(String accountname, String protocol,
                          String recipient) {
        return 1;
    }

    public int maxMessageSize(OTRContext context) {
        return 1000;
    }

    public void newFingerprint(OTRInterface us,
                               String accountname, String protocol, String username,
                               byte[] fingerprint) {
        System.out.println("\033[31mNew fingerprint is created.\033[0m");
    }

    public void stillSecure(OTRContext context, int is_reply) {
        System.out.println("\033[31mStill secure.\033[0m");
    }

    public void updateContextList() {
        System.out.println("\033[31mUpdating context list.\033[0m");
    }

    public void writeFingerprints() {
        System.out.println("\033[31mWriting fingerprints.\033[0m");
    }

    public String errorMessage(OTRContext context, int err_code) {
        if (err_code == OTRCallbacks.OTRL_ERRCODE_MSG_NOT_IN_PRIVATE) {
            return "You sent an encrypted message, but we finished" +
                    "the private conversation.";
        }
        return null;
    }

    public void handleMsgEvent(int msg_event,
                               OTRContext context, String message) {
        if (msg_event == OTRCallbacks.OTRL_MSGEVENT_CONNECTION_ENDED) {
            System.out.println("\033[31mThe private connection has already ended.\033[0m");
        } else if (msg_event == OTRCallbacks.OTRL_MSGEVENT_RCVDMSG_NOT_IN_PRIVATE) {
            System.out.println("\033[31mWe received an encrypted message, but we are not in" +
                    "encryption state.\033[0m");
        }
    }

    public void handleSmpEvent(int smpEvent,
                               OTRContext context, int progress_percent, String question) {
        if (smpEvent == OTRCallbacks.OTRL_SMPEVENT_ASK_FOR_SECRET) {
            System.out.println("\033[31mThe other side has initialized SMP." +
                    " Please respond with /rs.\033[0m");
        } else if (smpEvent == OTRCallbacks.OTRL_SMPEVENT_ASK_FOR_ANSWER) {
            System.out.println("\033[31mThe other side has initialized SMP, with question:" +
                    question + ", " +
                    " Please respond with /rs.\033[0m");
        } else if (smpEvent == OTRCallbacks.OTRL_SMPEVENT_SUCCESS) {
            System.out.println("\033[31mSMP succeeded.\033[0m");
        } else if (smpEvent == OTRCallbacks.OTRL_SMPEVENT_FAILURE) {
            System.out.println("\033[31mSMP failed.\033[0m");
        }


    }

}