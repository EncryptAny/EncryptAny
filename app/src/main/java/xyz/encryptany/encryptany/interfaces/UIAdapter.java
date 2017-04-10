package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapter {

    void setUIListener(UIListener uiListener);
    void giveMessage(Message msg);
    void updateMessages(Message[] msgs);
    void minimizeUI();
    void showChathead();
    void setActiveApp(String new_app_name);
    void setSourceName(String src_name);
    void setDestName(String dest_name);
}
