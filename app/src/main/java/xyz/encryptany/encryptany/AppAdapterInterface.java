package xyz.encryptany.encryptany;

import xyz.encryptany.encryptany.Listeners.MessagesUpdatedListener;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface AppAdapterInterface {
    public void setMessageUpdatedListener(MessagesUpdatedListener listener);
    public boolean inputMessage(Message message);

}
