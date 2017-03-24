package xyz.encryptany.encryptany.testing;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import xyz.encryptany.encryptany.concrete.EncryptedMessage;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.MessageSentListener;

/**
 * Created by Max on 1/31/2017.
 *
 * UIAdapter that just does stuff for testing purposes.
 */

public class FakeUIAdapter implements UIAdapter {
    private static final int DELAY_SECS = 5;
    private MessageSentListener msl = null;
    Message[] msgs = null;

    private final Message dummyMsg = new EncryptedMessage("this is encrpted txt", "maxwell", "4ab", new Date());

    public static void fakeDelay() {
        try {
            for (int i=0; i!=DELAY_SECS; ++i) {
                Log.d("MAXWELL", "" + (DELAY_SECS-i) + " Seconds Left Until Interaction");
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public FakeUIAdapter() {
    }

    @Override
    public void setMessageSentListener(MessageSentListener msl) {
        this.msl = msl;
        fakeDelay();
        msl.sendMessage(dummyMsg);
    }

    @Override
    public void setMessages(Message[] msgs) {
        this.msgs = msgs;
    }
}
