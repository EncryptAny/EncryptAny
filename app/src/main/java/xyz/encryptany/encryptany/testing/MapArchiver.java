package xyz.encryptany.encryptany.testing;

import android.util.Log;

import net.sqlcipher.Cursor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by max on 4/14/17.
 */

public class MapArchiver implements Archiver {

    private static final String TAG = "Archiver";
    private static final boolean DEBUG = false;

    // Tree map due to potential sorting capabilities
    Set<String> sentMessages = new HashSet<>();

    @Override
    public void archiveMessage(Message message) {
//        if (DEBUG) {
//            Log.d(TAG, "adding message with date: " + message.getDate());
//        }
//        msgs.put(message.getDate(), message);
        Log.d(TAG, "archiveMessage not yet implemented");
    }

    @Override
    public void setMessageExists(String UUID) {
        sentMessages.add(UUID);
    }

    @Override
    public boolean doesMessageExist(String UUID) {
        boolean msgExists = sentMessages.contains(UUID);
        if (DEBUG && msgExists) {
            Log.d(TAG, "excluded msg with uuid: " + UUID );
        }
        return msgExists;
    }

    @Override
    public Cursor retrieveAppMessages(String app) {
        return null;
    }
}
