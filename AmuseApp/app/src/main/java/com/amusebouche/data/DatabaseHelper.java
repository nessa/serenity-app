package com.amusebouche.data;


import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public static final String DATABASE_NAME = "Amuse.db";

    private final Context mContext;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }


    /**
     * Create a new recipe in the database with its categories, ingredients and directions.
     *
     * @param recipe Recipe to insert in the database
     */
    public void createRecipe(Recipe recipe) {

        SQLiteDatabase db = getWritableDatabase();

        // Create the database row for the project and keep its unique identifier
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_ID, recipe.getId());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, recipe.getTitle());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_OWNER, recipe.getOwner());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE, recipe.getLanguage());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH,
                recipe.getTypeOfDish());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY,
                recipe.getDifficulty());
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP,
                dateFormat.format(recipe.getCreatedTimestamp()));
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP,
                dateFormat.format(recipe.getUpdatedTimestamp()));
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
        recipeId = db.insert(RecipeContract.TABLE_NAME, null, recipeValues);

        // Set database id in recipe
        recipe.setDatabaseId(Objects.toString(recipeId));

        // Insert the database rows for the categories of the recipe in the database
        for (int i = 0; i < recipe.getCategories().size(); i++) {
            RecipeCategory category = (RecipeCategory) recipe.getCategories().get(i);

            ContentValues categoryValues = new ContentValues();
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID,
                    recipeId);
            categoryValues.put(RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_CATEGORY_NAME,
                    category.getName());
            db.insert(RecipeCategoryContract.TABLE_NAME, null, categoryValues);
        }

        // Insert the database rows for the ingredients of the recipe in the database
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            RecipeIngredient ingredient = (RecipeIngredient) recipe.getIngredients().get(i);

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID,
                    recipeId);
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_SORT_NUMBER,
                    ingredient.getSortNumber());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_INGREDIENT_NAME,
                    ingredient.getName());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_QUANTITY,
                    ingredient.getQuantity());
            ingredientValues.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_MEASUREMENT_UNIT,
                    ingredient.getMeasurementUnit());
            db.insert(RecipeIngredientContract.TABLE_NAME, null, ingredientValues);
        }

        // Insert the database rows for the directions of the recipe in the database
        for (int i = 0; i < recipe.getDirections().size(); i++) {
            RecipeDirection direction = (RecipeDirection) recipe.getDirections().get(i);

            ContentValues directionValues = new ContentValues();
            directionValues.put(RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID,
                    recipeId);
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
            db.insert(RecipeDirectionContract.TABLE_NAME, null, directionValues);
        }
    }


    /**
     * Deletes the specified recipe from the database.
     *
     * @param recipe the recipe to remove
     */
    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase db = getWritableDatabase();

        /* Delete the database rows for the categories, ingredients and directions of the recipe
         * in the database and the proper recipe
         */
        if (recipe.getDatabaseId() != "") {
            db.delete(RecipeCategoryContract.TABLE_NAME,
                    RecipeCategoryContract.RecipeCategoryEntry.COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            db.delete(RecipeIngredientContract.TABLE_NAME,
                    RecipeIngredientContract.RecipeIngredientEntry.COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            db.delete(RecipeDirectionContract.TABLE_NAME,
                    RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });

            db.delete(RecipeContract.TABLE_NAME,
                    RecipeContract.RecipeEntry._ID +"=?",
                    new String[] { String.valueOf(recipe.getDatabaseId()) });
        }
    }

    /**
     * Gets the specified recipe from the database.
     *
     * @param recipeId the API identifier of the project to get
     * @return the specified recipe
     */
    public Recipe getRecipe(String recipeId) {
        // Gets the database in the current database helper in read-only mode
        SQLiteDatabase db = getReadableDatabase();
        Recipe recipe = null;


        ArrayList<RecipeCategory> categories = new ArrayList<>();
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        ArrayList<RecipeDirection> directions = new ArrayList<>();

        // Get all categories with this recipe id
        Cursor catCursor = db.query(RecipeCategoryContract.TABLE_NAME,
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
        Cursor ingCursor = db.query(RecipeIngredientContract.TABLE_NAME,
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
        Cursor dirCursor = db.query(RecipeDirectionContract.TABLE_NAME,
                null,
                RecipeDirectionContract.RecipeDirectionEntry.COLUMN_NAME_RECIPE_ID + "=?",
                new String[] { String.valueOf(recipeId) },
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
        Cursor projCursor = db.query(RecipeContract.TABLE_NAME,
                null,
                RecipeContract.RecipeEntry.COLUMN_NAME_ID + "=?",
                new String[] { String.valueOf(recipeId) },
                null,
                null,
                null);
        try {
            /* Get the value for each column for the database row pointed by
             * the cursor using the getColumnIndex method of the cursor and
             * use it to initialize a Project object by database row */
            recipe = new Recipe(
                    projCursor.getString(projCursor.getColumnIndex(RecipeContract.RecipeEntry._ID)),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_ID)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TITLE)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_OWNER)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_LANGUAGE)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TYPE_OF_DISH)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_DIFFICULTY)
                    ),
                    dateFormat.parse(projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_CREATED_TIMESTAMP))
                    ),
                    dateFormat.parse(projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_UPDATED_TIMESTAMP))
                    ),
                    projCursor.getFloat(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_COOKING_TIME)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE)
                    ),
                    projCursor.getInt(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_TOTAL_RATING)
                    ),
                    projCursor.getInt(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_USERS_RATING)
                    ),
                    projCursor.getInt(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_SERVINGS)
                    ),
                    projCursor.getString(projCursor.getColumnIndex(
                            RecipeContract.RecipeEntry.COLUMN_NAME_SOURCE)
                    ),
                    categories,
                    ingredients,
                    directions
            );
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        projCursor.close();

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
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getRecipes(Integer limit, Integer offset) {
        return getRecipes(limit, offset, null);
    }

    /**
     * Gets the list of recipes from the database.
     *
     * @param limit Max number of results to return
     * @param offset Position of the first element to return
     * @param where Conditions to match
     * @return the current recipes from the database.
     */
    public ArrayList<Recipe> getRecipes(Integer limit, Integer offset, String where) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Gets the database in the current database helper in read-only mode
        SQLiteDatabase db = getReadableDatabase();

        // After the query, the cursor points to the first database row
        // returned by the request.
        String[] columns = {"_id"};
        Cursor projCursor = db.query(RecipeContract.TABLE_NAME,
                columns,
                where,
                null,
                null,
                null,
                null,
                "limit " + limit + " offset " + offset);
        while (projCursor.moveToNext()) {
            long recipeId = projCursor.getLong(projCursor.getColumnIndex(
                    RecipeContract.RecipeEntry._ID)
            );

            Recipe recipe = getRecipe(Objects.toString(recipeId));
            recipes.add(recipe);
        }
        projCursor.close();

        return recipes;
    }


    /**
     * Initialize example data to show when the application is first installed.
     */
    private void initializeExampleData() {
        String jsonData = null;
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
        // Create the database to contain the data for the projects
        db.execSQL(RecipeContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeCategoryContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeIngredientContract.SQL_CREATE_TABLE);
        db.execSQL(RecipeDirectionContract.SQL_CREATE_TABLE);

        // TODO: Undo this!
        initializeExampleData();
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
