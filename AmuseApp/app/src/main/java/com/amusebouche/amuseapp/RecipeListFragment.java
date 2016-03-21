package com.amusebouche.amuseapp;

import android.content.Intent;
import android.graphics.Point;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private MainActivity mMainActivity;
    private Boolean mOffline = true;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ActionButton mAddButton;

    // Services variables
    private DatabaseHelper mDatabaseHelper;


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

        mMainActivity = (MainActivity) getActivity();
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

        RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_recipe_list,
                container, false);

        // Set gridview parameters
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int screenWidth = screenSize.x;

        mProgressBar = (ProgressBar) mLayout.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mGridView = (GridView) mLayout.findViewById(R.id.gridview);
        mGridView.setAdapter(new GridviewCellAdapter(getActivity(), this, screenWidth));

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

                        i.putParcelableArrayListExtra(PARCELABLE_RECIPES_KEY, mMainActivity.getRecipes());
                        i.putExtra(CURRENT_PAGE_KEY, mMainActivity.getCurrentPage());
                        i.putExtra(LIMIT_PER_PAGE_KEY, mMainActivity.getLimitPerPage());

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
     *  Inflate the menu items to use them in the action bar
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_recipe_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be
     * reconstructed in a new instance of its process is restarted.
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);/*
        outState.putParcelableArrayList(PARCELABLE_RECIPES_KEY, mRecipes);
        outState.getInt(CURRENT_PAGE_KEY, mCurrentPage);
        outState.getInt(LIMIT_PER_PAGE_KEY, mLimitPerPage);*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Download next group of recipes
     *
     * TODO: Check if we have to download more, or we already end (no more recipes to return)
     */
    public void downloadNextRecipes() {
        mMainActivity.setCurrentPage(mMainActivity.getCurrentPage() + 1);
        new GetRecipes().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetRecipes extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... result) {
            if (mOffline) {
                mMainActivity.getRecipes().addAll(mDatabaseHelper.getRecipes(mMainActivity.getLimitPerPage(),
                        mMainActivity.getCurrentPage() * mMainActivity.getLimitPerPage()));
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
                                mMainActivity.getRecipes().add(new Recipe(results.getJSONObject(i)));
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