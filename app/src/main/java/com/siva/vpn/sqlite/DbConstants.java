package com.siva.vpn.sqlite;

import android.provider.BaseColumns;

/**
 * This sqlite is only user when user want to display the unread  notification
 */
public class DbConstants implements BaseColumns {

    // commons
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    public static final String COLUMN_NAME_NULLABLE = null;

    // notification
    public static final String NOTIFICATION_TABLE_NAME = "notifications";

    public static final String COLUMN_NOT_TITLE = "not_title";
    public static final String COLUMN_NOT_MESSAGE = "not_message";
    public static final String COLUMN_NOT_READ_STATUS = "not_status";
    public static final String COLUMN_NOT_CONTENT_URL = "content_url";

    public static final String SQL_CREATE_NOTIFICATION_ENTRIES =
            "CREATE TABLE " + NOTIFICATION_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NOT_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_CONTENT_URL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_READ_STATUS + TEXT_TYPE +
                    " )";


    public static final String SQL_DELETE_NOTIFICATION_ENTRIES =
            "DROP TABLE IF EXISTS " + NOTIFICATION_TABLE_NAME;


}
