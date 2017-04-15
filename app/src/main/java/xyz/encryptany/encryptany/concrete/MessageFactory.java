package xyz.encryptany.encryptany.concrete;

import android.util.Log;

import net.sqlcipher.Cursor;

import java.util.Date;
import java.util.UUID;

import xyz.encryptany.encryptany.concrete.DataBase.MessageArchiverContract;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 1/26/2017.
 */

public class MessageFactory {

    private static final boolean DEBUG = true;
    private static final String TAG = "MessageFactory";

    private static final String INIT_MESSAGE_TEXT = "LetsStartAnOTRConvo";

    public MessageFactory() {

    }

    public Message createNewInitMessage(String otherParticipant, String app) {
        long time = getNow();
        return new EncryptedMessage(INIT_MESSAGE_TEXT, otherParticipant, app, time, getUUID(),null);
    }

    public Message createNewMessage(String message, String otherParticipant, String app, long unixDate, String uuid,String iv) {
        if (DEBUG) {
            Log.d(TAG, "createNewMessage passed in unixDate: " + unixDate);
        }
        return new EncryptedMessage(message, otherParticipant, app, unixDate, uuid,iv);
    }

    public Message createNewMessage(String message, String otherParticipant, String app) {
        long time = getNow();

        return new EncryptedMessage(message, otherParticipant, app, time, getUUID(),null);
    }

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

    private static long getNow() {
        long time = (new Date()).getTime();
        return time;
    }

    public Message[] reconstructConversationMessages(Cursor cursor) {
        cursor.moveToFirst();
        Message messages[] = new Message[cursor.getCount()];
        String author;
        String message;
        long date;
        String application;

        for (int i = 0; i < cursor.getCount(); ++i) {
            author = cursor.getString(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_AUTHOR));
            message = cursor.getString(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_MESSAGE));
            date = cursor.getLong(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_DATE));
            application = cursor.getString(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_APPLICATION));
            messages[i] = new UnencryptedMessage(message, author, application, date);
        }

        return messages;
    }


}
