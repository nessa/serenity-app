package com.amusebouche.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.DatabaseHelper;

/**
 * GetRecipe loader class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Loader class that connects asynchronously with the database to get the recipe stored.
 */
public class GetRecipeLoader extends AsyncTaskLoader<Recipe> {

    // Service
    private DatabaseHelper mDatabaseHelper;

    // Query data
    private String mId;

    /**
     * Constructor
     * @param context Context from where its called this loader
     * @param id Query parameter
     */
    public GetRecipeLoader(Context context, String id) {
        super(context);

        mDatabaseHelper = new DatabaseHelper(context);
        mId = id;
    }

    /**
     * Call to the database helper to get the recipe with a given database identifier.
     * @return The recipe stored in the database.
     */
    @Override
    public Recipe loadInBackground() {
        if (mDatabaseHelper.existRecipeWithAPIId(mId)) {
            return mDatabaseHelper.getRecipeByAPIId(mId);
        } else {
            return null;
        }
    }

    @Override
    public void deliverResult(Recipe data) {
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
