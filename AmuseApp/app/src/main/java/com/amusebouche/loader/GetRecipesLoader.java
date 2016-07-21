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
 * GetRecipes loader class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Loader class that connects asynchronously with the database to get the recipes stored.
 */
public class GetRecipesLoader extends AsyncTaskLoader<List<Recipe>> {

    private DatabaseHelper mDatabaseHelper;

    private List<Recipe> mRecipes;
    public boolean hasResult = false;

    private int mPage;
    private int mLimit;
    private ArrayList<Pair<String, ArrayList<String>>> mParams;

    public GetRecipesLoader(Context context, int page, int limit,
                            ArrayList<Pair<String, ArrayList<String>>> params) {
        super(context);

        mDatabaseHelper = new DatabaseHelper(context);
        mPage = page;
        mLimit = limit;
        mParams = params;
    }

    /**
     * Call to the database helper to get the recipes give a limit, an offset and the
     * filter parameters.
     * @return The recipes stored in the database.
     */
    @Override
    public List<Recipe> loadInBackground() {
        return mDatabaseHelper.getRecipes(mLimit, mPage * mLimit, mParams);
    }

    @Override
    public void deliverResult(List<Recipe> data) {
        mRecipes = data;
        hasResult = true;
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
        if (hasResult) {
            onReleaseResources(mRecipes);
            mRecipes = null;
            hasResult = false;
        }
    }

    protected void onReleaseResources(List<Recipe> data) {
        //nothing to do.
    }
}
