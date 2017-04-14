package xyz.encryptany.encryptany.testing;

import android.util.Log;

import net.sqlcipher.Cursor;

import java.util.Map;
import java.util.TreeMap;

import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by max on 4/14/17.
 */

public class MapArchiver implements Archiver {

    private static final String TAG = "Archiver";
    private static final boolean DEBUG = true;

    // Tree map due to potential sorting capabilities
    Map<Long, Message> msgs = new TreeMap<>();

    @Override
    public void archiveMessage(Message message) {
        if (DEBUG) {
            Log.d(TAG, "adding message with date: " + message.getDate());
        }
        msgs.put(message.getDate(), message);
    }

    @Override
    public boolean doesMessageExist(long dateOfMessage) {
        boolean msgExists = msgs.containsKey(dateOfMessage);
        if (DEBUG && msgExists) {
            Log.d(TAG, "excluded msg with date: " + dateOfMessage );
        }
        return msgExists;
    }

    @Override
    public Cursor retrieveAppMessages(String app) {
        return null;
    }
}
