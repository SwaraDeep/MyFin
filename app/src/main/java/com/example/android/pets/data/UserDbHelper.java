package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Thal Marc on 4/4/2017.
 */

public class UserDbHelper extends SQLiteOpenHelper {

    private final static String SQL_CREATE_USER_TABLES = "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " ( " +
                                                        UserContract.UserEntry._ID + " INTEGER PRIMARY KEY, " +
                                                        UserContract.UserEntry.COLUMN_USER_NAME + " TEXT, " +
                                                        UserContract.UserEntry.COLUMN_USER_GENRE + " INTEGER, "+
                                                        UserContract.UserEntry.COLUMN_USER_AMOUNT + " INTEGER, " +
                                                        UserContract.UserEntry.COLUMN_USER_PAID + " INTEGER, " +
                                                        UserContract.UserEntry.COLUMN_USER_MOBILE + " TEXT, " +
                                                        UserContract.UserEntry.COLUMN_USER_ADDRESS + " TEXT)";

    private final static String SQL_DELETE_USER_TABLES = "DROP TABLE IF EXISTS" + UserContract.UserEntry.TABLE_NAME;

    public final static String DATABASE_NAME = "user.db";
    public final static int DATABASE_VERSION = 1;

    public UserDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // When the DB is first create
        Log.v("Database:", "created as " + SQL_CREATE_USER_TABLES );
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // When we need to update the DB
        Log.v("Database:", "created as " + SQL_CREATE_USER_TABLES );
        sqLiteDatabase.execSQL(SQL_DELETE_USER_TABLES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
