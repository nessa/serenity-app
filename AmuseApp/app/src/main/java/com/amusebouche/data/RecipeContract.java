package com.amusebouche.data;


import android.provider.BaseColumns;


/**
 * RecipeContract class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class represents a contract for a recipes table containing recipes for
 * which to count categories, ingredients adn directions.
 */
public final class RecipeContract {

    /**
     * Contains the name of the table to create that contains the row counters.
     */
    public static final String TABLE_NAME = "recipe";

    /**
     * Contains the SQL query to use to create the table containing the projects.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + RecipeContract.TABLE_NAME +
            " ("+ RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_ID +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_TITLE +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_OWNER +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP +
            " DATE,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP +
            " DATE,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME +
            " FLOAT DEFAULT 0,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE +
            " STRING,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING +
            " INTEGER DEFAULT 0,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING +
            " INTEGER DEFAULT 0,"
            + RecipeEntry.COLUMN_NAME_AVERAGE_RATING +
            " FLOAT DEFAULT 0,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS +
            " INTEGER DEFAULT 0,"
            + RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE + " TEXT);";


    /**
     * This class represents the rows for an entry in the project table. The
     * primary key is the _id column from the BaseColumn class.
     */
    public static abstract class RecipeEntry implements BaseColumns {

        // Recipe API id
        public static final String COLUMN_NAME_ID = "id";

        // Name of the recipe as shown in the application.
        public static final String COLUMN_NAME_TITLE = "title";

        // User owner of this recipe
        public static final String COLUMN_NAME_OWNER = "owner";

        // Language of this recipe
        public static final String COLUMN_NAME_LANGUAGE = "language";

        // Type of dish of this recipe
        public static final String COLUMN_NAME_TYPE_OF_DISH = "type_of_dish";

        // Difficulty of this recipe
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty";

        // Date when this recipe was created
        public static final String COLUMN_NAME_CREATED_TIMESTAMP = "created_timestamp";

        // Last time when this recipe was updated
        public static final String COLUMN_NAME_UPDATED_TIMESTAMP = "updated_timestamp";

        // Time that will take to prepare this recipe
        public static final String COLUMN_NAME_COOKING_TIME = "cooking_time";

        // Image of this recipe
        public static final String COLUMN_NAME_IMAGE = "image";

        // Total rating of this recipe
        public static final String COLUMN_NAME_TOTAL_RATING = "total_rating";

        // Number of users that have rated this recipe
        public static final String COLUMN_NAME_USERS_RATING = "users_rating";

        // Average rating
        public static final String COLUMN_NAME_AVERAGE_RATING = "average_rating";

        // Number of servings of this recipe
        public static final String COLUMN_NAME_SERVINGS = "servings";

        // Source of this recipe
        public static final String COLUMN_NAME_SOURCE = "source";
    }
}
