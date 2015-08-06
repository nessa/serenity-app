package com.amusebouche.amuseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.data.DatabaseHelper;
import com.amusebouche.services.ServiceHandler;
import com.melnykov.fab.FloatingActionButton;
import com.amusebouche.data.Recipe;

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
    private RelativeLayout mLayout;
    private ProgressDialog infoDialog;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private LinearLayout mProgressBarLayout;
    private ArrayList<Recipe> mRecipes;
    private Integer mRecipesPage;
    private Integer mLastGridviewPosition;
    private DatabaseHelper mDatabaseHelper;
    private Boolean mOffline = true;


    /**
     * visibleThreshold – The minimum amount of items to have below your current scroll position, before loading more.
     currentPage – The current page of data you have loaded
     previousTotal – The total number of items in the dataset after the last load
     loading – True if we are still waiting for the last set of data to load.
     */
    private int mVisibleThreshold = 5;
    private int mCurrentPage = 1;
    private int mPreviousTotal = 0;
    private boolean mLoading = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate()");

        // Gets the database helper to access the database for the application
        mDatabaseHelper = new DatabaseHelper(getActivity().getApplicationContext());

        // Prevent errors
        mRecipes = new ArrayList<>();

        // Calling async task to get json
        if (savedInstanceState == null || !savedInstanceState.containsKey("recipes")) {
            mLastGridviewPosition = 0;

            // Get recipes from main activity
            MainActivity x = (MainActivity)getActivity();
            mRecipes = x.getRecipes();
        } else {
            mRecipes = savedInstanceState.getParcelableArrayList("recipes");
            mLastGridviewPosition = savedInstanceState.getInt("last_position");

            for (int i = 0; i < mRecipes.size(); i++) {
                mRecipes.get(i).printString();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        mGridView.setAdapter(new GridviewCellAdapter(getActivity(), this, screen_width, mRecipes));

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

        /*
        if (mLastGridviewPosition != 0) {
            mGridView.smoothScrollToPosition(mLastGridviewPosition);
        }*/

        FloatingActionButton addButton = (FloatingActionButton) mLayout.findViewById(R.id.fab);

        // TODO: If user is logged in, addButton must be visible
        if (false) {
            addButton.setVisibility(View.GONE);
        }

        // TODO: addButton on click must go to create activity

        return mLayout;
    }

    @Override
    public void onResume() {
        Log.i(getClass().getSimpleName(), "onResume()");
        super.onResume();
        changeActionButton();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("recipes", mRecipes);

        // This fails!! Maybe we could add it to shared preferences??
        //outState.putInt("last_position", mGridView.getFirstVisiblePosition());


        //outState.putInt("present_page", mRecipesPage);
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


    public void setNewRecipes() {
        final int positionToSave = mGridView.getFirstVisiblePosition();
        GridviewCellAdapter adapter = (GridviewCellAdapter) mGridView.getAdapter();
        adapter.setRecipes(mRecipes);

        mGridView.post(new Runnable() {

            @Override
            public void run() {
                mGridView.setSelection(positionToSave);
            }
        });

        mGridView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                if (mGridView.getFirstVisiblePosition() == positionToSave) {
                    mGridView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void makeFavorite(View v) {
        Log.v("INFO", "Favorite " + v.getTag());
    }

    /* Instead of using the action bar method setNavigationMode, we define specifically the
     * buttons to show. We call this method when the app is created the first time (onResume)
     * and every time it appears again (onHiddenChange). */
    private void changeActionButton() {
        MainActivity x = (MainActivity) getActivity();
        x.setDrawerIndicatorEnabled(true);
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetRecipes extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("INFO", "PRE EXECUTE");

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... result) {
            if (mOffline) {
                mDatabaseHelper.initializeExampleData();
                mRecipes.addAll(mDatabaseHelper.getRecipes(12, mCurrentPage*12));
            } else {
                // Create service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Check internet connection
                if (sh.checkInternetConnection(getActivity().getApplicationContext())) {

                    // Make a request to url and getting response
                    String jsonStr = sh.makeServiceCall("recipes/", ServiceHandler.GET);

                    Log.d("RESPONSE", "> " + jsonStr);

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

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Log.d("INFO", "POST EXECUTE");
            GridviewCellAdapter adapter = (GridviewCellAdapter) mGridView.getAdapter();
            adapter.notifyDataSetChanged();

            mProgressBar.setVisibility(View.GONE);
        }
    }

}