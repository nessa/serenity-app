package com.amusebouche.fragments;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Point;
import android.app.Activity;
import android.app.Fragment;
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
import android.widget.TextView;

import com.amusebouche.activities.EditionActivity;
import com.amusebouche.adapters.GridviewCellAdapter;
import com.amusebouche.activities.MainActivity;
import com.amusebouche.activities.R;
import com.amusebouche.loader.GetRecipesLoader;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.RequestHandler;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.RetrofitServiceGenerator;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
public class RecipeListFragment extends Fragment implements Callback<ResponseBody>,
    LoaderManager.LoaderCallbacks {

    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";

    // The loader's unique id. Loader ids are specific to the Activity or
    // Fragment in which they reside.
    private static final int LOADER_ID = 1;

    // Data variables
    private MainActivity mMainActivity;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ActionButton mAddButton;
    private TextView mErrorMessage;

    // Behaviour variables
    private boolean mNoConnectionError;
    private boolean mFirstLoadLaunched;

    // Services variables
    private Loader mLoader;
    private Call<ResponseBody> mRequestCall;

    // LIFECYCLE METHODS

    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragment activity (DetailActivity)
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
        //mDatabaseHelper = new DatabaseHelper(getActivity().getApplicationContext());

        mMainActivity = (MainActivity) getActivity();

        mFirstLoadLaunched = false;
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


        mErrorMessage = (TextView) mLayout.findViewById(R.id.error_message);

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

        if (mMainActivity.getRecipes().size() == 0 && !mFirstLoadLaunched) {
            loadRecipes();
            mFirstLoadLaunched = true;
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

    // AUXILIAR METHODS

    /**
     * Download next group of recipes
     */
    public void downloadNextRecipes() {
        mMainActivity.setCurrentPage(mMainActivity.getCurrentPage() + 1);
        loadRecipes();
    }

    public void loadRecipes() {
        this.onPreExecute();

        switch (mMainActivity.getMode()) {
            default:
            case MainActivity.DOWNLOADED_RECIPES_MODE:
            case MainActivity.MY_RECIPES_MODE:
                if (mLoader == null || !mLoader.isStarted()) {
                    mLoader = getActivity().getLoaderManager().initLoader(LOADER_ID, null, this);
                    mLoader.forceLoad();
                } else {
                    mLoader = getActivity().getLoaderManager().restartLoader(LOADER_ID, null, this);
                    mLoader.forceLoad();
                }
                break;
            case MainActivity.NEW_RECIPES_MODE:
                AmuseAPI api = RetrofitServiceGenerator.createService(AmuseAPI.class);

                mRequestCall = api.get(RequestHandler.buildGetURL(
                    RequestHandler.API_RECIPES_ENDPOINT,
                    RequestHandler.buildParams(mMainActivity.getCurrentPage() + 1,
                        mMainActivity.getFilterParams())));

                // Asynchronous call
                mRequestCall.enqueue(this);
        }
    }

    private void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);

        mNoConnectionError = false;
        mErrorMessage.setText(getString(R.string.recipe_list_no_recipes_message));
    }

    private void onPostExecute() {
        // Notify the adapter the new elements added
        GridviewCellAdapter adapter = (GridviewCellAdapter) mGridView.getAdapter();
        adapter.notifyDataSetChanged();

        mProgressBar.setVisibility(View.GONE);

        if (mMainActivity.getRecipes().size() == 0) {
            if (mNoConnectionError) {
                mErrorMessage.setText(getString(R.string.recipe_list_no_connection_message));
            }

            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    // RETROFIT REQUEST METHODS

    /**
     * Retrofit call response on success
     * @param call Retrofit call
     * @param response Response data
     */
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        String data = "";

        // Get response data
        if (response.body() != null) {
            try {
                data = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Build objects from response data
        if (!Objects.equals(data, "")) {
            try {
                JSONObject jObject = new JSONObject(data);
                JSONArray results = jObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    Recipe r = new Recipe(results.getJSONObject(i));
                    r.setIsOnline(true);
                    mMainActivity.getRecipes().add(r);
                }

                this.onPostExecute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // No data from request
            this.onPostExecute();
        }

    }

    /**
     * Retrofit call response on error
     * @param call Retrofit call
     * @param t Exception
     */
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        this.onPostExecute();
    }

    /**
     * Abort service handler requests (loader and retrofit call)
     */
    public void forceStop() {
        if (mLoader != null) {
            mLoader.cancelLoad();
        }
        if (mRequestCall != null) {
            mRequestCall.cancel();
        }
    }


    // LOADER METHODS

    /**
     * Creates the loader
     * @param id Loader id
     * @param args Loader arguments
     * @return Loader object
     */
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
        if (mMainActivity.getMode() == MainActivity.DOWNLOADED_RECIPES_MODE) {
            return new GetRecipesLoader(getActivity().getApplicationContext(),
                mMainActivity.getCurrentPage(), mMainActivity.getLimitPerPage(),
                mMainActivity.getFilterParams());
        } else {
            // My recipes
            // TODO Get recipes with owner = me (if user is logged in) or without owner
            return new GetRecipesLoader(getActivity().getApplicationContext(),
                mMainActivity.getCurrentPage(), mMainActivity.getLimitPerPage(),
                mMainActivity.getFilterParams());
        }
    }

    /**
     * Loader finishes its execution
     * @param loader Loader object
     * @param data Loader data: list of recipes
     */
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == LOADER_ID) {
            mMainActivity.getRecipes().addAll((List<Recipe>) data);
            this.onPostExecute();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}

}