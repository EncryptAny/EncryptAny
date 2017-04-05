package xyz.encryptany.encryptany.interfaces;

import net.sqlcipher.Cursor;

import java.util.Iterator;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Archiver {
    public void archiveMessage(Message message);
    public Cursor retrieveAppMessages(String app);

}
