package com.amusebouche.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.amusebouche.data.Recipe;
import com.amusebouche.services.DatabaseHelper;

/**
 * SaveRecipe loader class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Loader class that connects asynchronously with the database to save a recipe (creates a new
 * one or updates an existing one).
 */
public class SaveRecipeLoader extends AsyncTaskLoader<Void> {

    private DatabaseHelper mDatabaseHelper;

    private Recipe mRecipe;

    public SaveRecipeLoader(Context context, Recipe recipe) {
        super(context);

        mDatabaseHelper = new DatabaseHelper(context);
        mRecipe = recipe;
    }

    /**
     * Call to the database helper to save the given recipe.
     * @return The recipes stored in the database.
     */
    @Override
    public Void loadInBackground() {
        /* Update recipe if:
         * - It's from API and exists a recipe with its API id in the database
         * - It's not from API and it exists in database
         * Otherwise, create a new recipe
         */
        if ((mRecipe.getIsOnline() && mDatabaseHelper.existRecipeWithAPIId(mRecipe.getId())) ||
                (!mRecipe.getIsOnline() && mRecipe.getDatabaseId() != null &&
                        !mRecipe.getDatabaseId().equals(""))) {
            mDatabaseHelper.updateRecipe(mRecipe);
        } else {
            mDatabaseHelper.createRecipe(mRecipe);
        }

        return null;
    }

    @Override
    public void deliverResult(Void data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}
