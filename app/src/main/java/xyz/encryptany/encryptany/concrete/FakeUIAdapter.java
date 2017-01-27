package xyz.encryptany.encryptany.concrete;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.MessageSentListener;

/**
 * Created by Max on 1/31/2017.
 *
 * UIAdapter that just does stuff for testing purposes.
 */

public class FakeUIAdapter implements UIAdapter {
    private MessageSentListener msl = null;
    Message[] msgs = null;

    private static void fakeDelay() {
        try {
            for (int i=0; i!=5; ++i) {
                Log.d("MAXWELL", "" + (5-i) + " Seconds Left Until Interaction");
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public FakeUIAdapter() {
    }

    @Override
    public void setMessaageSentListener(MessageSentListener msl) {
        this.msl = msl;
        fakeDelay();
        msl.sendMessage(new EncryptedMessage("this is encrpted txt", "maxwell", "4ab", 12345));
    }

    @Override
    public void setMessages(Message[] msgs) {
        this.msgs = msgs;
    }
}
