package com.amusebouche.services;


import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.RecipeCategoryContract;
import com.amusebouche.data.RecipeContract;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeDirectionContract;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.RecipeIngredientContract;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * RecipesDatabaseHelper class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class helps open, create, and upgrade the database file containing the
 * recipes and their categories, ingredients and directions.
 *
 * See github.com/CindyPotvin/RowCounter
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // When you change the database schema, this database version must be incremented
    public static final int DATABASE_VERSION = 1;
    // The name of the database file on the file system
    public static final String DATABASE_NAME = "amuse.sqlite";

    private static SQLiteDatabase mDatabase;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Create a new recipe in the database with its categories, ingredients and directions.
     *
     * @param recipe Recipe to insert in the database
     */
    public void createRecipe(Recipe recipe) {
        mDatabase = this.getWritableDatabase();

        // Create the database row for the recipe and keep its unique identifier
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_ID, recipe.getId());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, recipe.getTitle());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_OWNER, recipe.getOwner());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE, recipe.getLanguage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH,
                recipe.getTypeOfDish());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY,
                recipe.getDifficulty());

        if (recipe.getCreatedTimestamp() == null) {
            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP,
                dateFormat.format(new Date()));
            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP,
                dateFormat.format(new Date()));
        } else {
            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP,
                dateFormat.format(recipe.getCreatedTimestamp()));
            recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP,
                dateFormat.format(recipe.getUpdatedTimestamp()));
        }

        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME,
                recipe.getCookingTime());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE, recipe.getImage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING,
                recipe.getTotalRating());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING,
                recipe.getUsersRating());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS, recipe.getServings());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE, recipe.getSource());

        long recipeId;
        recipeId = mDatabase.insert(RecipeContract.TABLE_NAME, null, recipeValues);

        // Set database id in recipe
        recipe.setDatabaseId(Objects.toString(recipeId));

        // Insert the database rows for the categories of the recipe in the database
        for (int i = 0; i < recipe.getCategories().size(); i++) {
            RecipeCategory category = recipe.getCategories().get(i);

            ContentValues categoryValues = new ContentValues();
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID,
                    recipeId);
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID,
                    recipe.getId());
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME,
                    category.getName());
            mDatabase.insert(RecipeCategoryContract.TABLE_NAME, null, categoryValues);
        }

        // Insert the database rows for the ingredients of the recipe in the database
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            RecipeIngredient ingredient = recipe.getIngredients().get(i);

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID,
                    recipeId);
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID,
                    recipe.getId());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER,
                    ingredient.getSortNumber());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME,
                    ingredient.getName());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY,
                    ingredient.getQuantity());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT,
                    ingredient.getMeasurementUnit());
            mDatabase.insert(RecipeIngredientContract.TABLE_NAME, null, ingredientValues);
        }

        // Insert the database rows for the directions of the recipe in the database
        for (int i = 0; i < recipe.getDirections().size(); i++) {
            RecipeDirection direction = recipe.getDirections().get(i);

            ContentValues directionValues = new ContentValues();
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry._COLUMN_NAME_RECIPE_ID,
                    recipeId);
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID,
                    recipe.getId());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER,
                    direction.getSortNumber());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION,
                    direction.getDescription());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE,
                    direction.getImage());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO,
                    direction.getVideo());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME,
                    direction.getTime());
            mDatabase.insert(RecipeDirectionContract.TABLE_NAME, null, directionValues);
        }

        mDatabase.close();
    }

    public void updateRecipe(Recipe recipe) {
        mDatabase = this.getWritableDatabase();

        // Delete all subelements
        mDatabase.delete(RecipeCategoryContract.TABLE_NAME,
                RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID +"=?",
                new String[] { String.valueOf(recipe.getDatabaseId()) });

        mDatabase.delete(RecipeIngredientContract.TABLE_NAME,
                RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID +"=?",
                new String[] { String.valueOf(recipe.getDatabaseId()) });

        mDatabase.delete(RecipeDirectionContract.TABLE_NAME,
                RecipeDirectionContract.RecipeDirectionEntry._COLUMN_NAME_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipe.getDatabaseId())});

        // Update the database row for the recipe and keep its unique identifier
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_ID, recipe.getId());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, recipe.getTitle());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_OWNER, recipe.getOwner());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE, recipe.getLanguage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH,
                recipe.getTypeOfDish());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY,
                recipe.getDifficulty());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP,
            dateFormat.format(new Date()));
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME,
                recipe.getCookingTime());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE, recipe.getImage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING,
                recipe.getTotalRating());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING,
                recipe.getUsersRating());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS, recipe.getServings());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE, recipe.getSource());

        long recipeId = mDatabase.update(RecipeContract.TABLE_NAME,
                recipeValues,
                RecipeContract.RecipeEntry._ID +"=?",
                new String[] { String.valueOf(recipe.getDatabaseId()) });


        // Reset subelements

        // Insert the database rows for the categories of the recipe in the database
        for (int i = 0; i < recipe.getCategories().size(); i++) {
            RecipeCategory category = recipe.getCategories().get(i);

            ContentValues categoryValues = new ContentValues();
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID,
                    recipeId);
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID,
                    recipe.getId());
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME,
                    category.getName());
            mDatabase.insert(RecipeCategoryContract.TABLE_NAME, null, categoryValues);
        }

        // Insert the database rows for the ingredients of the recipe in the database
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            RecipeIngredient ingredient = recipe.getIngredients().get(i);

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID,
                    recipeId);
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID,
                    recipe.getId());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER,
                    ingredient.getSortNumber());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME,
                    ingredient.getName());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY,
                    ingredient.getQuantity());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT,
                    ingredient.getMeasurementUnit());
            mDatabase.insert(RecipeIngredientContract.TABLE_NAME, null, ingredientValues);
        }

        // Insert the database rows for the directions of the recipe in the database
        for (int i = 0; i < recipe.getDirections().size(); i++) {
            RecipeDirection direction = recipe.getDirections().get(i);

            ContentValues directionValues = new ContentValues();
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry._COLUMN_NAME_RECIPE_ID,
                    recipeId);
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID,
                    recipe.getId());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER,
                    direction.getSortNumber());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION,
                    direction.getDescription());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE,
                    direction.getImage());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO,
                    direction.getVideo());
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME,
                    direction.getTime());
            mDatabase.insert(RecipeDirectionContract.TABLE_NAME, null, directionValues);
        }

        mDatabase.close();
    }



    /**
     * Deletes the specified recipe from the database.
     *
     * @param recipe the recipe to remove
     */
    public void deleteRecipe(Recipe recipe) {
        mDatabase = this.getWritableDatabase();

        /* Delete the database rows for the categories, ingredients and directions of the recipe
         * in the database and the proper recipe
         */
        if (!recipe.getDatabaseId().equals("")) {
            mDatabase.delete(RecipeCategoryContract.TABLE_NAME,
                    RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            mDatabase.delete(RecipeIngredientContract.TABLE_NAME,
                    RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            mDatabase.delete(RecipeDirectionContract.TABLE_NAME,
                    RecipeDirectionContract.RecipeDirectionEntry._COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            mDatabase.delete(RecipeContract.TABLE_NAME,
                    RecipeContract.RecipeEntry._ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });
        }

        mDatabase.close();
    }

    /**
     * Gets the specified recipe from the database.
     *
     * @param databaseRecipeId the database identifier of the recipe to get
     * @return the specified recipe
     */
    public Recipe getRecipeByDatabaseId(String databaseRecipeId) {
        // Gets the database in the current database helper in read-only mode
        mDatabase = this.getReadableDatabase();
        Recipe recipe = null;

        ArrayList<RecipeCategory> categories = new ArrayList<>();
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        ArrayList<RecipeDirection> directions = new ArrayList<>();

        // Get all categories with this recipe id
        Cursor catCursor = mDatabase.query(RecipeCategoryContract.TABLE_NAME,
                null,
                RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(databaseRecipeId) },
                null,
                null,
                null);
        while (catCursor.moveToNext()) {
            RecipeCategory category = new RecipeCategory(
                    catCursor.getString(catCursor.getColumnIndex(
                            RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME)
                    )
            );
            categories.add(category);
        }
        catCursor.close();


        // Get all ingredients with this recipe id
        Cursor ingCursor = mDatabase.query(RecipeIngredientContract.TABLE_NAME,
                null,
                RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(databaseRecipeId) },
                null,
                null,
                null);
        while (ingCursor.moveToNext()) {
            RecipeIngredient ingredient = new RecipeIngredient(
                    ingCursor.getInt(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER)
                    ),
                    ingCursor.getString(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME)
                    ),
                    ingCursor.getFloat(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY)
                    ),
                    ingCursor.getString(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT)
                    )
            );
            ingredients.add(ingredient);
        }
        ingCursor.close();


        // Get all directions with this recipe id
        Cursor dirCursor = mDatabase.query(RecipeDirectionContract.TABLE_NAME,
                null,
                RecipeDirectionContract.RecipeDirectionEntry._COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { databaseRecipeId },
                null,
                null,
                null);
        while (dirCursor.moveToNext()) {
            RecipeDirection direction = new RecipeDirection(
                    dirCursor.getInt(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO)
                    ),
                    dirCursor.getFloat(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME)
                    )
            );
            directions.add(direction);
        }
        dirCursor.close();

        /* After the query, the cursor points to the first database row
         * returned by the request */
        Cursor recipeCursor = mDatabase.query(RecipeContract.TABLE_NAME,
                null,
                RecipeContract.RecipeEntry._ID + "=?",
                new String[] { String.valueOf(databaseRecipeId) },
                null,
                null,
                null);

        recipeCursor.moveToNext();

        try {
            /* Get the value for each column for the database row pointed by
             * the cursor using the getColumnIndex method of the cursor and
             * use it to initialize a Recipe object by database row */
            recipe = new Recipe(
                    recipeCursor.getString(recipeCursor.getColumnIndex(RecipeContract.RecipeEntry._ID)),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_ID)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TITLE)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_OWNER)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY)
                    ),
                    dateFormat.parse(recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP))
                    ),
                    dateFormat.parse(recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP))
                    ),
                    recipeCursor.getFloat(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE)
                    ),
                    recipeCursor.getInt(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING)
                    ),
                    recipeCursor.getInt(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING)
                    ),
                    recipeCursor.getInt(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE)
                    ),
                    categories,
                    ingredients,
                    directions
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        recipeCursor.close();

        mDatabase.close();
        return recipe;
    }

    /**
     * Gets the specified recipe from the database.
     *
     * @param recipeId the API identifier of the recipe to get
     * @return the specified recipe
     */
    public Recipe getRecipeByAPIId(String recipeId) {
        // Gets the database in the current database helper in read-only mode
        mDatabase = this.getReadableDatabase();
        Recipe recipe = null;

        ArrayList<RecipeCategory> categories = new ArrayList<>();
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        ArrayList<RecipeDirection> directions = new ArrayList<>();

        // Get all categories with this recipe id
        Cursor catCursor = mDatabase.query(RecipeCategoryContract.TABLE_NAME,
                null,
                RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        while (catCursor.moveToNext()) {
            RecipeCategory category = new RecipeCategory(
                    catCursor.getString(catCursor.getColumnIndex(
                                    RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME)
                    )
            );
            categories.add(category);
        }
        catCursor.close();


        // Get all ingredients with this recipe id
        Cursor ingCursor = mDatabase.query(RecipeIngredientContract.TABLE_NAME,
                null,
                RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        while (ingCursor.moveToNext()) {
            RecipeIngredient ingredient = new RecipeIngredient(
                    ingCursor.getInt(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER)
                    ),
                    ingCursor.getString(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME)
                    ),
                    ingCursor.getFloat(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY)
                    ),
                    ingCursor.getString(ingCursor.getColumnIndex(
                                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT)
                    )
            );
            ingredients.add(ingredient);
        }
        ingCursor.close();


        // Get all directions with this recipe id
        Cursor dirCursor = mDatabase.query(RecipeDirectionContract.TABLE_NAME,
                null,
                RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { recipeId },
                null,
                null,
                null);
        while (dirCursor.moveToNext()) {
            RecipeDirection direction = new RecipeDirection(
                    dirCursor.getInt(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_SORT_NUMBER)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_DESCRIPTION)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_IMAGE)
                    ),
                    dirCursor.getString(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_VIDEO)
                    ),
                    dirCursor.getFloat(dirCursor.getColumnIndex(
                                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_TIME)
                    )
            );
            directions.add(direction);
        }
        dirCursor.close();

        /* After the query, the cursor points to the first database row
         * returned by the request */
        Cursor recipeCursor = mDatabase.query(RecipeContract.TABLE_NAME,
                null,
                //RecipeContract.RecipeEntry.COLUMN_NAME_ID + "=?",
                RecipeContract.RecipeEntry._ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);

        recipeCursor.moveToNext();

        try {
            /* Get the value for each column for the database row pointed by
             * the cursor using the getColumnIndex method of the cursor and
             * use it to initialize a Recipe object by database row */
            recipe = new Recipe(
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_ID)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_ID)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_TITLE)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_OWNER)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY)
                    ),
                    dateFormat.parse(recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP))
                    ),
                    dateFormat.parse(recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP))
                    ),
                    recipeCursor.getFloat(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE)
                    ),
                    recipeCursor.getInt(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING)
                    ),
                    recipeCursor.getInt(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING)
                    ),
                    recipeCursor.getInt(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS)
                    ),
                    recipeCursor.getString(recipeCursor.getColumnIndex(
                                    RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE)
                    ),
                    categories,
                    ingredients,
                    directions
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        recipeCursor.close();

        mDatabase.close();
        return recipe;
    }

    /**
     * Gets the list of recipes from the database.
     *
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getRecipes() {
        return getRecipes(10, 0, null);
    }

    /**
     * Gets the list of recipes from the database.
     *
     * @param limit Max number of results to return
     * @param offset Position of the first element to return
     * @param whereParams Conditions to match
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getRecipes(Integer limit, Integer offset,
                                        ArrayList<Pair<String, ArrayList<String>>> whereParams) {

        ArrayList<Recipe> recipes = new ArrayList<>();

        // Gets the database in the current database helper in read-only mode
        mDatabase = getReadableDatabase();

        String where = "";
        String ordering = "";
        int count = 0;
        if (whereParams != null) {
            for (Pair item : whereParams) {
                String key = (String) item.first;

                for (String value : (ArrayList<String>) item.second) {

                    // Avoid "page_size": only for REST requests
                    if (!key.equals(RequestHandler.API_PARAM_PAGE_SIZE)) {

                        if (key.equals(RequestHandler.API_PARAM_ORDERING)) {
                            if (value.startsWith("-")) {
                                ordering = value.substring(1, value.length()) + " DESC";
                            } else {
                                ordering = value + " ASC";
                            }
                        } else {
                            // If the key is not ORDERING, then we will set a new WHERE condition,
                            // so we need to add an AND to join it
                            if (count > 0) {
                                where += " AND ";
                            }
                        }

                        if (key.equals(RequestHandler.API_PARAM_LANGUAGE)) {
                            where += RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE +
                                " = UPPER('" + value + "') ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_TITLE)) {
                            where += RecipeContract.RecipeEntry.COLUMN_NAME_TITLE +
                                " LIKE LOWER('%" + value + "%') ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_DIFFICULTY)) {
                            where += RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY +
                                " = '" + value + "' ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_TYPE_OF_DISH)) {
                            where += RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH +
                                " = '" + value + "' ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_CATEGORY)) {
                            where += "EXISTS (SELECT " +
                                RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID +
                                " FROM " + RecipeCategoryContract.TABLE_NAME +
                                " WHERE " + RecipeContract.TABLE_NAME + "." +
                                RecipeContract.RecipeEntry._ID + " = " +
                                RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID +
                                " AND " + RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME +
                                " LIKE ('%" + value + "%')) ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_DISLIKE_CATEGORY)) {
                            where += "NOT EXISTS (SELECT " +
                                RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID +
                                " FROM " + RecipeCategoryContract.TABLE_NAME +
                                " WHERE " + RecipeContract.TABLE_NAME + "." +
                                RecipeContract.RecipeEntry._ID + " = " +
                                RecipeCategoryContract.RecipeCategoryEntry._COLUMN_NAME_RECIPE_ID +
                                " AND " + RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME +
                                " LIKE ('%" + value + "%')) ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_DISLIKE_INGREDIENT)) {
                            where += "NOT EXISTS (SELECT " +
                                RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID +
                                " FROM " + RecipeIngredientContract.TABLE_NAME +
                                " WHERE " + RecipeContract.TABLE_NAME + "." +
                                RecipeContract.RecipeEntry._ID + " = " +
                                RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID +
                                " AND " + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME +
                                " LIKE ('%" + value + "%')) ";
                        }

                        if (key.equals(RequestHandler.API_PARAM_INGREDIENT)) {
                            where += "EXISTS (SELECT " +
                                RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID +
                                " FROM " + RecipeIngredientContract.TABLE_NAME +
                                " WHERE " + RecipeContract.TABLE_NAME + "." +
                                RecipeContract.RecipeEntry._ID + " = " +
                                RecipeIngredientContract.RecipeIngredientEntry._COLUMN_NAME_RECIPE_ID +
                                " AND " + RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME +
                                " LIKE ('%" + value + "%')) ";
                        }

                        count += 1;
                    }
                }
            }
        }

        // After the query, the cursor points to the first database row
        // returned by the request.
        String[] columns = {"_id"};
        Cursor recipeCursor = mDatabase.query(RecipeContract.TABLE_NAME,
                columns,
                where,
                null,
                null,
                null,
                ordering,
                offset + ", " + limit);
        //"limit " + limit + " offset " + offset);
        while (recipeCursor.moveToNext()) {
            long recipeId = recipeCursor.getLong(recipeCursor.getColumnIndex(
                    RecipeContract.RecipeEntry._ID)
            );

            Recipe recipe = getRecipeByDatabaseId(Objects.toString(recipeId));
            recipes.add(recipe);
        }
        recipeCursor.close();

        mDatabase.close();
        return recipes;
    }


    public Integer countRecipes() {
        mDatabase = getReadableDatabase();

        String count = "SELECT count(*) FROM " + RecipeContract.TABLE_NAME;
        Cursor mCursor = mDatabase.rawQuery(count, null);
        mCursor.moveToFirst();
        int recipesCount = mCursor.getInt(0);
        mCursor.close();

        mDatabase.close();
        return recipesCount;
    }


    /**
     * Initialize example data to show when the application is first installed.
     */
    public void initializeExampleData() {
        String jsonData;
        try {
            InputStream is = mContext.getAssets().open("recipes.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonData = new String(buffer, "UTF-8");

            try {
                JSONArray results = new JSONArray(jsonData);
                for (int i = 0; i < results.length(); i++) {
                    Recipe recipe = new Recipe(results.getJSONObject(i));
                    this.createRecipe(recipe);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates the underlying database with the SQL_CREATE_TABLE queries from
     * the contract classes to create the tables and initialize the data.
     * The onCreate is triggered the first time someone tries to access
     * the database with the getReadableDatabase or
     * getWritableDatabase methods.
     *
     * @param db the database being accessed and that should be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database to contain the data for the recipes
        db.execSQL(RecipeContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeCategoryContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeIngredientContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeDirectionContract.SQL_CREATE_TABLE);
    }

    /**
     *
     * This method must be implemented if your application is upgraded and must
     * include the SQL query to upgrade the database from your old to your new
     * schema.
     *
     * @param db the database being upgraded.
     * @param oldVersion the current version of the database before the upgrade.
     * @param newVersion the version of the database after the upgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        Log.i(DatabaseHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion);
    }
}
