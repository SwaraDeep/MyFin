package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.pets.data.UserContract.CONTENT_AUTHORITY;
import static com.example.android.pets.data.UserContract.PATH_USERS;


/**
 * Created by Thal Marc on 4/10/2017.
 */

public class UserProvider extends ContentProvider {

    //Database Helper instance
    private UserDbHelper mDbHelper ;

    /** Tag for the log messages */
    public static final String LOG_TAG = UserProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int USERS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int USER_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY,"users", USERS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_USERS + "/#", USER_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new UserDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                // For the USERS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.

                cursor = database.query(UserContract.UserEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case USER_ID:
                // For the USER_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //We set an notification to know when the data is changed
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return insertUser(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case USER_ID:
                // For the USER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowDeleted ;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                // Delete all rows that match the selection and selection args
                rowDeleted = database.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                if(rowDeleted != 0 ){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return  rowDeleted;
            case USER_ID:
                // Delete a single row given by the ID in the URI
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowDeleted = database.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                if(rowDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return UserContract.UserEntry.CONTENT_LIST_TYPE;
            case USER_ID:
                return UserContract.UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertUser(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        Log.v("Values:", values.getAsString(UserContract.UserEntry.COLUMN_USER_NAME) +
                values.getAsString(UserContract.UserEntry.COLUMN_USER_MOBILE) +
                values.getAsString(UserContract.UserEntry.COLUMN_USER_ADDRESS) +
                values.getAsString(UserContract.UserEntry.COLUMN_USER_AMOUNT) +
                values.getAsString(UserContract.UserEntry.COLUMN_USER_GENRE));

        String name = values.getAsString(UserContract.UserEntry.COLUMN_USER_NAME);

        if(name == null){
            throw  new IllegalArgumentException("Pet required a name");
        }

        String amount = values.getAsString(UserContract.UserEntry.COLUMN_USER_AMOUNT);
        if(amount == null){
            throw new IllegalArgumentException("Pet required a breed");
        }

        int gender = values.getAsInteger(UserContract.UserEntry.COLUMN_USER_GENRE);
        if(gender != UserContract.UserEntry.GENDER_MALE && gender != UserContract.UserEntry.GENDER_FEMALE && gender != UserContract.UserEntry.GENDER_UNKNOWN){
            throw new IllegalArgumentException("Gender Can't be Unknown");
        }
       /* int paid = values.getAsInteger(UserContract.UserEntry.COLUMN_USER_PAID);
        if(paid <= 0){
            throw new IllegalArgumentException("Weight can't be 0 or less...");
        }*/
       //String address = values.getAsString(UserContract.UserEntry.COLUMN_USER_ADDRESS);
        long id = database.insert(UserContract.UserEntry.TABLE_NAME,null,values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if(values.containsKey(UserContract.UserEntry.COLUMN_USER_NAME) ){
            if(values.getAsString(UserContract.UserEntry.COLUMN_USER_NAME) == null){
                throw new IllegalArgumentException("Pet required name");
            }
        }
        if(values.containsKey(UserContract.UserEntry.COLUMN_USER_AMOUNT)){
            if(values.getAsString(UserContract.UserEntry.COLUMN_USER_AMOUNT) == null){
                throw new IllegalArgumentException("Pet required breed");
            }
        }
        if(values.containsKey(UserContract.UserEntry.COLUMN_USER_GENRE)){
            if(values.getAsInteger(UserContract.UserEntry.COLUMN_USER_GENRE) != UserContract.UserEntry.GENDER_FEMALE && values.getAsInteger(UserContract.UserEntry.COLUMN_USER_GENRE) != UserContract.UserEntry.GENDER_MALE && values.getAsInteger(UserContract.UserEntry.COLUMN_USER_GENRE) != UserContract.UserEntry.GENDER_UNKNOWN){
                throw new IllegalArgumentException("Pet required a valid Genre");
            }
        }
        if(values.containsKey(UserContract.UserEntry.COLUMN_USER_PAID)){
            if(values.getAsInteger(UserContract.UserEntry.COLUMN_USER_PAID) <=0 ){
                throw new IllegalArgumentException("Pet required Weight > 0");
            }
        }

        if(values.size() == 0 ){
            return 0;
        }


        int rowUpdate = database.update(UserContract.UserEntry.TABLE_NAME,values,selection,selectionArgs);

        if(rowUpdate != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return  rowUpdate;
    }

}
