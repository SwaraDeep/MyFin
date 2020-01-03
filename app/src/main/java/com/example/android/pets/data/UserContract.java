package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by Thal Marc on 4/4/2017.
 */

public final class UserContract {

    /** Creation of the Uri */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USERS = "users";





    /* No Instantiable */
    private UserContract(){}

    /* Pet Table */
    public static class UserEntry implements BaseColumns{


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        /**
         * The MIME type of the {} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        /**
         * The MIME type of the {} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String TABLE_NAME = "users";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_USER_NAME = "name";
        public final static String COLUMN_USER_AMOUNT = "amount";
        public final static String COLUMN_USER_GENRE = "genre";
        public final static String COLUMN_USER_PAID = "paid";
       // public final static String COLUMN_USER_REMAINING = "rem";
        public final static String COLUMN_USER_MOBILE = "mobile";
        public final static String COLUMN_USER_ADDRESS = "address";



        public final static  int GENDER_UNKNOWN = 0;
        public final static  int GENDER_MALE = 1;
        public final static  int GENDER_FEMALE  = 2;

    }
}
