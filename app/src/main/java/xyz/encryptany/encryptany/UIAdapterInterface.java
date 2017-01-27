package xyz.encryptany.encryptany;

import xyz.encryptany.encryptany.Listeners.MessageSendingListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface UIAdapterInterface {

    public void displayMessage(Message message);

    public void setMessageSendingListener(MessageSendingListener listener);

}
