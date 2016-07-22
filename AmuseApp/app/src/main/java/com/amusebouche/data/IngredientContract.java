package com.amusebouche.data;

import android.provider.BaseColumns;

/**
 * IngredientContract class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class represents a contract for an ingredients table containing ingredients.
 */
public class IngredientContract {

    /**
     * Contains the name of the table to create that contains the row counters.
     */
    public static final String TABLE_NAME = "ingredient";

    /**
     * Contains the SQL query to use to create the table containing the projects.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + IngredientContract.TABLE_NAME +
        " ("+ IngredientContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
        + IngredientContract.IngredientEntry.COLUMN_NAME_TRANSLATION +
        " STRING,"
        + IngredientContract.IngredientEntry.COLUMN_NAME_LANGUAGE +
        " STRING,"
        + IngredientContract.IngredientEntry.COLUMN_NAME_TIMESTAMP +
        " DATE,"
        + IngredientContract.IngredientEntry.COLUMN_NAME_CATEGORIES +
        " TEXT);";


    /**
     * This class represents the rows for an entry in the project table. The
     * primary key is the _id column from the BaseColumn class.
     */
    public static abstract class IngredientEntry implements BaseColumns {

        // Ingredient translation
        public static final String COLUMN_NAME_TRANSLATION = "translation";

        // Language of this ingredient
        public static final String COLUMN_NAME_LANGUAGE = "language";

        // Last time when this ingredient was updated
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

        // Categories of this ingredient
        public static final String COLUMN_NAME_CATEGORIES = "categories";
    }
}
