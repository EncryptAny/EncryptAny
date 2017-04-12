package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapter {
    enum UIStatus {
        INACTIVE,
        ACTIVE,
        READY,
        AWAITING_ENCRYPT,
        BUSY
    }
    enum UIWindowState {
        SHOWING,
        MINIMIZED,
        EDITING_TEXT,
        CLOSED
    }
    void setUIListener(UIListener uiListener);
    void giveMessage(Message msg);
    void updateMessages(Message[] msgs);
    void setActiveAppName(String new_app_name);
    void setAuthorName(String author_name);
    void setRecipientName(String recipient_name);
    UIWindowState getUIWindowState();
    UIStatus getUIStatus();
    void setUIStatus(UIStatus uistatus);
    void setUIWindowState_Minimized();

}
