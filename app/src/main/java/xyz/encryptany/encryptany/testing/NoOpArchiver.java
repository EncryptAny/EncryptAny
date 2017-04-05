package xyz.encryptany.encryptany.testing;

import net.sqlcipher.Cursor;

import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by max on 4/5/17.
 */

public class NoOpArchiver implements Archiver {
    @Override
    public void archiveMessage(Message message) {

    }

    @Override
    public Cursor retrieveAppMessages(String app) {
        return null;
    }
}
