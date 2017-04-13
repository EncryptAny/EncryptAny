package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.AppListener;

/**
 * Created by dakfu on 1/26/2017.
*/


public interface AppAdapter {
    void sendMessage(Message message);
    void setMessageUpdatedListener(AppListener listener);
}
