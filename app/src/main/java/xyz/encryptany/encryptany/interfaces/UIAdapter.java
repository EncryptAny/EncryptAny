package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.MessageSentListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapter {

    void setMessageSentListener(MessageSentListener msl);
    void setMessages(Message[] msg);
}
