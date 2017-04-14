package xyz.encryptany.encryptany.concrete;

import net.sqlcipher.Cursor;

import java.util.Date;

import xyz.encryptany.encryptany.concrete.DataBase.MessageArchiverContract;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 1/26/2017.
 */

public class MessageFactory {

    public MessageFactory(){

    }

    public Message createNewInitMessage(String otherParticipant,String app){
        return new EncryptedMessage("LetsStartAnOTRConvo",otherParticipant, app, new Date().getTime());
    }
    public Message createNewMessage(String message, String otherParticipant, String app, long unixDate){
        return new EncryptedMessage(message, otherParticipant, app, unixDate);
    }
    public Message createNewMessage(String message, String otherParticipant, String app){
        return new EncryptedMessage(message, otherParticipant, app, new Date().getTime());
    }
    public Message[] reconstructConversationMessages(Cursor cursor){
        cursor.moveToFirst();
        Message messages[] = new Message[cursor.getCount()];
        String author;
        String message;
        long date;
        String application;

        for(int i = 0; i < cursor.getCount();++i){


            author = cursor.getString(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_AUTHOR));
            message = cursor.getString(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_MESSAGE));
            date = cursor.getLong(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_DATE));
            application = cursor.getString(cursor.getColumnIndex(MessageArchiverContract.MessageEntry.COLUMN_NAME_APPLICATION));


            messages[i] = new UnencryptedMessage(message,author,application,date);
        }

        return messages;
    }


}
