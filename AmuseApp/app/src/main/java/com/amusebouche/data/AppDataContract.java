package com.amusebouche.data;

/**
 * AppDataContract class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class represents a contract for an app_data table containing
 * keys and values needed for the app.
 */
public class AppDataContract {
    /**
     * Contains the name of the table to create that contains the app data.
     */
    public static final String TABLE_NAME = "app_name";

    /**
     * Contains the SQL query to use to create the table containing the app data.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + AppDataContract.TABLE_NAME + " ("
            + AppDataContract.AppDataEntry.COLUMN_NAME_KEY +
            " STRING PRIMARY KEY NOT NULL,"
            + AppDataContract.AppDataEntry.COLUMN_NAME_VALUE +
            " STRING);";

    /**
     * This class represents the rows for an entry in the app_data table.
     * The primary key is the key column.
     */
    public static abstract class AppDataEntry {

        /**
         * Identifier key of the data in the database
         */
        public static final String COLUMN_NAME_KEY = "key";

        /**
         * Value of the data
         */
        public static final String COLUMN_NAME_VALUE = "value";

    }
}
