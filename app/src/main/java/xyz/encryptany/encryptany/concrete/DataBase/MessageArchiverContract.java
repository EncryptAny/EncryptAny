package xyz.encryptany.encryptany.concrete.DataBase;

import android.provider.BaseColumns;

import net.sqlcipher.database.SQLiteDatabase;
/**
 * Created by dakfu on 3/15/2017.
 */

public final class MessageArchiverContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MessageArchiverContract() {}

    /* Inner class that defines the table contents */
    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "archive";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_APPLICATION = "application";
    }
}
