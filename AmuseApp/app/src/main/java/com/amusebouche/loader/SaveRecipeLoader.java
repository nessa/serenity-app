package com.amusebouche.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

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
        if (mRecipe.getDatabaseId() != null && !mRecipe.getDatabaseId().equals("")) {
            // Update
            mDatabaseHelper.updateRecipe(mRecipe);
        } else {
            // Create
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
        if (takeContentChanged()) {
            forceLoad();
        }
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

    protected void onReleaseResources(List<Recipe> data) {
        //nothing to do.
    }
}
