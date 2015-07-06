package com.amusebouche.data;


import android.provider.BaseColumns;

/**
 * RecipeDirectionContract class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class represents a contract for a recipe_direction table containing
 * directions for recipes. The recipe must exist before creating directions
 * since the direction have a foreign key to the recipe.
 */
public class RecipeDirectionContract {
    /**
     * Contains the name of the table to create that contains the directions.
     */
    public static final String TABLE_NAME = "recipe_direction";

    /**
     * Contains the SQL query to use to create the table containing the directions.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + RecipeDirectionContract.TABLE_NAME + " ("
            + RecipeDirectionContract.RecipeDirectionEntry._ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID +
            " STRING,"
            + RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER +
            " INTEGER DEFAULT 0,"
            + RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION +
            " STRING,"
            + RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE +
            " STRING,"
            + RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO +
            " STRING,"
            + RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME +
            " FLOAT DEFAULT 0,"
            + "FOREIGN KEY (" +
            RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID +
            ") REFERENCES recipes(" + RecipeContract.RecipeEntry._ID + "));";

    /**
     * This class represents the rows for an entry in the recipe_direction table.
     * The primary key is the _id column from the BaseColumn class.
     */
    public static abstract class RecipeDirectionEntry implements BaseColumns {

        /**
         * Identifier of the recipe to which the direction belongs
         */
        public static final String COLUMN_NAME_RECIPE_ID = "recipe_id";

        /**
         * Position of the this direction
         */
        public static final String COLUMN_NAME_SORT_NUMBER = "sort_number";

        /**
         * Description of this direction
         */
        public static final String COLUMN_NAME_DESCRIPTION = "description";

        /**
         * Image of this direction
         */
        public static final String COLUMN_NAME_IMAGE = "image";

        /**
         * Video of this direction
         */
        public static final String COLUMN_NAME_VIDEO = "video";

        /**
         * Time of this direction
         */
        public static final String COLUMN_NAME_TIME = "time";
    }
}
