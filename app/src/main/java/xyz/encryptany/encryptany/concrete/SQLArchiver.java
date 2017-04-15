package xyz.encryptany.encryptany.concrete;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;

import xyz.encryptany.encryptany.concrete.DataBase.MessageArchiverContract;
import xyz.encryptany.encryptany.concrete.DataBase.MessageArchiverDbHelper;
import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 2/24/2017.
 */

public class SQLArchiver implements Archiver {

    private SQLiteDatabase db;

    public SQLArchiver(Activity activity){
        InitializeSQLCipher(activity);
    }
    @Override
    public void archiveMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_MESSAGE, message.getMessage());
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_APPLICATION, message.getApp());
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_AUTHOR, message.getAuthor());
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_DATE, message.getDate());
// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert( MessageArchiverContract.MessageEntry.TABLE_NAME , null, values);
    }

    @Override
    public void setMessageExists(String UUID) {
        // TODO implement
    }

    @Override
    public boolean doesMessageExist(String UUID) {
        // TODO Implement
        return false;
    }

    public Cursor retrieveAllMessages(){
        Cursor cursor = db.rawQuery("SELECT * FROM '" + MessageArchiverContract.MessageEntry.TABLE_NAME + "';", null);
        //this cursor should be given to the message factory to contstruct the array of messages to be used in the interface adapter
        return cursor;
    }
    public Cursor retrieveAppMessages(String app){
        Cursor cursor = db.rawQuery("SELECT * FROM '" + MessageArchiverContract.MessageEntry.TABLE_NAME + "' WHERE '" + MessageArchiverContract.MessageEntry.COLUMN_NAME_APPLICATION + "'== '" + app + "';", null);
        //this cursor should be given to the message factory to contstruct the array of messages to be used in the interface adapter
        return cursor;
    }

    private void InitializeSQLCipher(Activity activity) {
        SQLiteDatabase.loadLibs(activity);
        //TODO: create user determined sql database password
        db = MessageArchiverDbHelper.getInstance(activity).getWritableDatabase("somePass");
    }
}
