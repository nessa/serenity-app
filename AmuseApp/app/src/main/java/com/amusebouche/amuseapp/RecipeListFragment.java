package com.amusebouche.amuseapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Point;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amusebouche.data.DatabaseHelper;
import com.amusebouche.services.ServiceHandler;
import com.amusebouche.data.Recipe;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Recipe list fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains a dynamic gridview with recipes (filtered or not).
 *
 * Related layouts:
 * - Content: fragment_recipe_list.xml
 */
public class RecipeListFragment extends Fragment {

    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";

    // Data variables
    private Recipe mRecipe;
    private ArrayList<Recipe> mRecipes;
    private Boolean mOffline = true;

    // UI variables
    private RelativeLayout mLayout;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ActionButton mAddButton;

    // Services variables

    private DatabaseHelper mDatabaseHelper;


    // Gridview scroll variables

    /**
     * The minimum amount of items to have below your current scroll position, before loading more.
     */
    private int mVisibleThreshold;
    /**
     * The current page of data you have loaded
     */
    private int mCurrentPage;
    /**
     * Number of elements to load in every page
     */
    private Integer mLimitPerPage;
    /**
     * The total number of items in the dataset after the last load
     */
    private int mPreviousTotal = 0;
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean mLoading = true;


    // LIFECYCLE METHODS

    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragemnt activity (DetailActivity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view
     * hierarchy instantiated.
     * @param savedInstanceState State of the fragment if it's being re-created.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Called to do initial creation of a fragment. This is called after onAttach and before
     * onCreateView.
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate()");

        // Gets the database helper to access the database for the application
        mDatabaseHelper = new DatabaseHelper(getActivity().getApplicationContext());

        // Prevent errors
        mRecipes = new ArrayList<Recipe>();

        // Calling async task to get json
        if (savedInstanceState == null || !savedInstanceState.containsKey("recipes")) {
            Log.d("FRAG", "NO SAVED INSTANCE");

            // Get recipes from main activity
            MainActivity x = (MainActivity)getActivity();
            mRecipes = x.getRecipes();
            mCurrentPage = x.getCurrentPage();
            mLimitPerPage = x.getLimitPerPage();
            mVisibleThreshold = mLimitPerPage / 4;
        } else {
            Log.d("FRAG", "SAVED INSTANCE");
            mRecipes = savedInstanceState.getParcelableArrayList("recipes");
            mCurrentPage = savedInstanceState.getInt("current_page");
            mLimitPerPage = savedInstanceState.getInt("limit");

            mVisibleThreshold = mLimitPerPage / 4;
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * onCreate and onActivityCreated, onViewStateRestored, onStart().
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If this fragment is being re-constructed from a previous saved
     *                           state as given here.
     * @return Return the View for the this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");

        mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_recipe_list,
                container, false);

        // Set gridview parameters
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int screen_width = screenSize.x;

        mProgressBar = (ProgressBar) mLayout.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mGridView = (GridView) mLayout.findViewById(R.id.gridview);
        mGridView.setAdapter(new GridviewCellAdapter(getActivity(), screen_width, mRecipes));

        mGridView.setOnScrollListener(new GridView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (mLoading) {
                    if (totalItemCount > mPreviousTotal) {
                        mLoading = false;
                        mPreviousTotal = totalItemCount;
                        mCurrentPage = mCurrentPage + 1;
                    }
                }

                if (!mLoading && (totalItemCount - visibleItemCount) <=
                        (firstVisibleItem + mVisibleThreshold)) {
                    // I load the next page of gigs using a background task,
                    // but you can call any function here.
                    new GetRecipes().execute();
                    mLoading = true;
                }
            }
        });


        mAddButton = (ActionButton) mLayout.findViewById(R.id.add_button);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddButton.hide();

                // Show FABs after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getActivity(), EditionActivity.class);

                        i.putParcelableArrayListExtra(PARCELABLE_RECIPES_KEY,
                                ((MainActivity) getActivity()).getRecipes());
                        i.putExtra(CURRENT_PAGE_KEY, ((MainActivity) getActivity()).getCurrentPage());
                        i.putExtra(LIMIT_PER_PAGE_KEY, ((MainActivity) getActivity()).getLimitPerPage());

                        startActivity(i);
                    }
                }, 1200);
            }
        });

        // TODO: If user is logged in, addButton must be visible
        if (true) {
            mAddButton.show();
        } else {
            mAddButton.setVisibility(View.INVISIBLE);
        }

        // TODO: addButton on click must go to create activity

        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAddButton.show();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        Log.i(getClass().getSimpleName(), "onResume()");
        super.onResume();
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be
     * reconstructed in a new instance of its process is restarted.
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PARCELABLE_RECIPES_KEY, mRecipes);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(getClass().getSimpleName(), "onHiddenChanged()");
        if (!hidden) {
            Log.i(getClass().getSimpleName(), "not hidden");
            changeActionButton();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void makeFavorite(View v) {
        Log.v("INFO", "Favorite " + v.getTag());
    }

    /**
     *  Instead of using the action bar method setNavigationMode, we define specifically the
     * buttons to show. We call this method when the app is created the first time (onResume)
     * and every time it appears again (onHiddenChange).
     */
    private void changeActionButton() {
        MainActivity x = (MainActivity) getActivity();
        ///x.setDrawerIndicatorEnabled(true);
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetRecipes extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("INFO", "PRE");

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... result) {
            if (mOffline) {
                mRecipes.addAll(mDatabaseHelper.getRecipes(mLimitPerPage, mCurrentPage * mLimitPerPage));
            } else {
                // Create service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Check internet connection
                if (sh.checkInternetConnection(getActivity().getApplicationContext())) {

                    // Make a request to url and getting response
                    String jsonStr = sh.makeServiceCall("recipes/", ServiceHandler.GET);

                    if (jsonStr != null) {
                        try {
                            JSONObject jObject = new JSONObject(jsonStr);
                            JSONArray results = jObject.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                mRecipes.add(new Recipe(results.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                } else {
                    Log.d("INFO", "ELSE");
                    // TODO: Get database recipes??
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Notify the adapter the new elements added
            GridviewCellAdapter adapter = (GridviewCellAdapter) mGridView.getAdapter();
            adapter.notifyDataSetChanged();

            mProgressBar.setVisibility(View.GONE);
        }
    }

}