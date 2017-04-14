package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.UIListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapter {
    enum UIWindowState {
        SHOWING,
        MINIMIZED,
        EDITING_TEXT,
        CLOSED
    }
    enum UIStatus {
        INACTIVE,
        ACTIVE,
        READY,
        AWAITING_CLICK,
        BUSY
    }
    void setUIListener(UIListener uiListener);
    void giveMessage(Message msg);
    void updateMessages(Message[] msgs);
    void setActiveAppName(String new_app_name);
    void setAuthorName(String author_name);
    void setRecipientName(String recipient_name);
    UIWindowState getUIWindowState();
    UIStatus getUIStatus();
    void needTextBoxClick();
    void deactivate();
    void activate();
    void waitUntilReady();
    void ready();

    void setUIWindowState_Minimized();
    void setUIWindowState_Showing();
    }
