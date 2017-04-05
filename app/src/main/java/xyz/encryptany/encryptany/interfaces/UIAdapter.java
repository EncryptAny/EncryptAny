package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapter {

    void setUIListener(UIListener uiListener);
    void setMessages(Message[] msg);
}
