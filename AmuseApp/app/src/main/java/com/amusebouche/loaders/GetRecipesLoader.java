package com.amusebouche.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
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

    // Service
    private DatabaseHelper mDatabaseHelper;

    // Query data
    private int mPage;
    private int mLimit;
    private ArrayList<Pair<String, ArrayList<String>>> mParams;

    /**
     * Constructor
     * @param context Context from where its called this loader
     * @param page Query parameter
     * @param limit Query parameter
     * @param params Rest of query parameters
     */
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
