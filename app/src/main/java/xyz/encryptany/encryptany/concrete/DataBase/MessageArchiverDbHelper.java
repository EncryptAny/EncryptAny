package xyz.encryptany.encryptany.concrete.DataBase;

/**
 * Created by dakfu on 3/15/2017.
 */
import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class MessageArchiverDbHelper extends SQLiteOpenHelper {
    private static MessageArchiverDbHelper instance;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MessageArchive.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MessageArchiverContract.MessageEntry.TABLE_NAME + " (" +
                    MessageArchiverContract.MessageEntry._ID + " INTEGER PRIMARY KEY," +
                    MessageArchiverContract.MessageEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + "," +
                    MessageArchiverContract.MessageEntry.COLUMN_NAME_MESSAGE + TEXT_TYPE + "," +
                    MessageArchiverContract.MessageEntry.COLUMN_NAME_DATE + TEXT_TYPE +","+
                    MessageArchiverContract.MessageEntry.COLUMN_NAME_APPLICATION + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MessageArchiverContract.MessageEntry.TABLE_NAME;

    public MessageArchiverDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static public synchronized MessageArchiverDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageArchiverDbHelper(context);
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
