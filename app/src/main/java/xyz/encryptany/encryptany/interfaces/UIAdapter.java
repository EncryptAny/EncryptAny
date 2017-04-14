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

    // Turns grey and does not allow the user to use the UI
    void disable();

    // means the UI is doneWaiting to recieve text
    // Activate removes transparency and makes clickable, indended after disable
    void enable();

    // Ready undoes the red and makes the ui doneWaiting to send again
    void doneWaiting();

    // Turns red and does not allow the user to use the UI
    void waitForProcessing();

    // Turns yellow and lets the user know that their action is needed to continue
    // should be followed by a call to doneWaiting
    void waitForUserSend();


    void setUIWindowState_Minimized();

    void setUIWindowState_Showing();
}
