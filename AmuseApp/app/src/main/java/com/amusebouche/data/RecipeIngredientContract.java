package com.amusebouche.data;


import android.provider.BaseColumns;

/**
 * RecipeIngredientContract class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class represents a contract for a recipe_ingredient table containing
 * ingredients for recipes. The recipe must exist before creating ingredients
 * since the ingredient have a foreign key to the recipe.
 */
public class RecipeIngredientContract {
    /**
     * Contains the name of the table to create that contains the ingredients.
     */
    public static final String TABLE_NAME = "recipe_ingredient";

    /**
     * Contains the SQL query to use to create the table containing the ingredients.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + RecipeIngredientContract.TABLE_NAME + " ("
            + RecipeIngredientContract.RecipeIngredientEntry._ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID +
            " STRING,"
            + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER +
            " INTEGER DEFAULT 0,"
            + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME +
            " STRING,"
            + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY +
            " FLOAT DEFAULT 0,"
            + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT +
            " STRING,"
            + "FOREIGN KEY (" +
            RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID +
            ") REFERENCES recipes(" + RecipeContract.RecipeEntry._ID + "));";

    /**
     * This class represents the rows for an entry in the recipe_ingredient table.
     * The primary key is the _id column from the BaseColumn class.
     */
    public static abstract class RecipeIngredientEntry implements BaseColumns {

        /**
         * Identifier of the recipe to which the ingredient belongs
         */
        public static final String COLUMN_NAME_RECIPE_ID = "recipe_id";

        /**
         * Position of the this ingredient
         */

        public static final String COLUMN_NAME_SORT_NUMBER = "sort_number";

        /**
         * Name of this ingredient
         */
        public static final String COLUMN_NAME_INGREDIENT_NAME = "name";

        /**
         * Needed quantity of this ingredient
         */
        public static final String COLUMN_NAME_QUANTITY = "quantity";

        /**
         * Measurement associated to the quantity
         */
        public static final String COLUMN_NAME_MEASUREMENT_UNIT = "measurement_unit";
    }
}
