package xyz.encryptany.encryptany.concrete;

import java.util.Iterator;

import java.io.File;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

import xyz.encryptany.encryptany.concrete.DataBase.MessageArchiverContract;
import xyz.encryptany.encryptany.concrete.DataBase.MessageArchiverDbHelper;
import xyz.encryptany.encryptany.interfaces.Archiver;
import xyz.encryptany.encryptany.interfaces.Message;

/**
 * Created by dakfu on 2/24/2017.
 */

public class SQLArchiver implements Archiver {

    public SQLArchiver(Activity activity){
        InitializeSQLCipher(activity);
    }
    @Override
    public void archiveMessage(Message message) {

    }

    

    private void InitializeSQLCipher(Activity activity) {
        SQLiteDatabase.loadLibs(activity);
        //TODO: create user determined sql database password
        SQLiteDatabase db = MessageArchiverDbHelper.getInstance(activity).getWritableDatabase("somePass");

        ContentValues values = new ContentValues();
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_AUTHOR, "Message Author");
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_MESSAGE, "Message Content");
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_DATE, "Date Message Sent");
        values.put(MessageArchiverContract.MessageEntry.COLUMN_NAME_APPLICATION, "Message Application Source");

        db.insert(MessageArchiverContract.MessageEntry.TABLE_NAME, null, values);


    }
}
