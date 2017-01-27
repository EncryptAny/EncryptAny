package xyz.encryptany.encryptany.interfaces;

import xyz.encryptany.encryptany.listeners.MessagesUpdatedListener;

/**
 * Created by dakfu on 1/26/2017.
*/


public interface AppAdapter {
    public boolean sendMessage(String message);
    public void setMessageUpdatedListener(MessagesUpdatedListener listener);
    public boolean inputMessage(Message message);
}
