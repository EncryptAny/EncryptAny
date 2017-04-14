package xyz.encryptany.encryptany.testing;

import net.sqlcipher.Cursor;

import java.util.Map;
import java.util.TreeMap;

import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by max on 4/14/17.
 */

public class MapArchiver implements Archiver {

    // Tree map due to potential sorting capabilities
    Map<Long, Message> msgs = new TreeMap<>();

    @Override
    public void archiveMessage(Message message) {
        msgs.put(message.getDate(), message);
    }

    @Override
    public boolean doesMessageExist(long dateOfMessage) {
        return msgs.containsKey(dateOfMessage);
    }

    @Override
    public Cursor retrieveAppMessages(String app) {
        return null;
    }
}
