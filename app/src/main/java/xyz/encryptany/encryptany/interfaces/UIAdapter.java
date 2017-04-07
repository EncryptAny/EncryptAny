package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapter {

    void setUIListener(UIListener uiListener);
    void giveMessage(Message msg);
    void updateMessages(Message[] msgs);
    void clearUI();
    void newActiveApp(String new_app_name);
}
