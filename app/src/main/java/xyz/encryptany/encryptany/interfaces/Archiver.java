package xyz.encryptany.encryptany.interfaces;

import net.sqlcipher.Cursor;

import java.util.Iterator;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Archiver {
    void archiveMessage(Message message);
    void setMessageExists(String UUID);
    boolean doesMessageExist(String UUID);
    Cursor retrieveAppMessages(String app);
}
