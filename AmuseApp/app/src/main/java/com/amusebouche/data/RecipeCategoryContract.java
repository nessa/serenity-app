package com.amusebouche.data;


import android.provider.BaseColumns;

/**
 * RecipeDirectionContract class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class represents a contract for a recipe_category table containing
 * categories for recipes. The recipe must exist before creating categories
 * since the category have a foreign key to the recipe.
 */
public class RecipeCategoryContract {
    /**
     * Contains the name of the table to create that contains the categories.
     */
    public static final String TABLE_NAME = "recipe_category";

    /**
     * Contains the SQL query to use to create the table containing the categories.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + RecipeCategoryContract.TABLE_NAME + " ("
            + RecipeCategoryContract.RecipeCategoryEntry._ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID +
            " STRING,"
            + RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME +
            " STRING,"
            + "FOREIGN KEY (" +
            RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID +
            ") REFERENCES recipes(" + RecipeContract.RecipeEntry._ID + "));";

    /**
     * This class represents the rows for an entry in the recipe_category table.
     * The primary key is the _id column from the BaseColumn class.
     */
    public static abstract class RecipeCategoryEntry implements BaseColumns {

        /**
         * Identifier of the recipe to which the category belongs
         */
        public static final String COLUMN_NAME_RECIPE_ID = "recipe_id";

        /**
         * Name of this category
         */
        public static final String COLUMN_NAME_CATEGORY_NAME = "name";

    }
}
