package com.amusebouche.fragments;


import android.graphics.Point;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.adapters.GridViewCellAdapter;
import com.amusebouche.activities.MainActivity;
import com.amusebouche.activities.R;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.RequestHandler;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
public class RecipeListFragment extends Fragment implements Callback<ResponseBody> {

    // Data variables
    private MainActivity mMainActivity;

    // UI
    private RecyclerView mGridView;
    private TextView mErrorMessage;

    // Behaviour variables
    private boolean mNoConnectionError;
    private boolean mFirstLoadLaunched;

    // Services variables
    private Call<ResponseBody> mRequestCall;
    private DatabaseHelper mDatabaseHelper;

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
        mDatabaseHelper = new DatabaseHelper(getActivity());
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

        // Set grid view parameters
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int screenWidth = screenSize.x;

        mErrorMessage = (TextView) mLayout.findViewById(R.id.error_message);

        mGridView = (RecyclerView) mLayout.findViewById(R.id.gridview);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),
            getResources().getInteger(R.integer.gridview_columns));
        mGridView.setLayoutManager(mLayoutManager);

        mGridView.setAdapter(new GridViewCellAdapter(getActivity(), this, screenWidth));

        if (mMainActivity.getRecipes().size() == 0 && !mFirstLoadLaunched) {
            loadRecipes();
            mFirstLoadLaunched = true;
        }

        // TODO: Check this!
        mMainActivity.showAddButton();

        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        Log.i(getClass().getSimpleName(), "onResume()");
        super.onResume();

        mMainActivity.showAddButton();
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
        super.onSaveInstanceState(outState);
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
                mMainActivity.getRecipes().addAll(mDatabaseHelper.getRecipes(
                        mMainActivity.getLimitPerPage(),
                        mMainActivity.getCurrentPage() * mMainActivity.getLimitPerPage(),
                        mMainActivity.getFilterParams()));

                this.onPostExecute();
                break;
            case MainActivity.NEW_RECIPES_MODE:
                AmuseAPI api = RetrofitServiceGenerator.createService(AmuseAPI.class);

                mRequestCall = api.getRecipes(RequestHandler.buildGetURL(
                    RequestHandler.API_RECIPES_ENDPOINT,
                    RequestHandler.buildParams(mMainActivity.getCurrentPage() + 1,
                        mMainActivity.getFilterParams())));

                // Asynchronous call
                mRequestCall.enqueue(this);
        }
    }

    private void onPreExecute() {
        mMainActivity.showLoadingIndicator();

        // Hide errors
        mErrorMessage.setVisibility(View.GONE);

        mNoConnectionError = false;
        mErrorMessage.setText(getString(R.string.recipe_list_no_recipes_message));
    }

    private void onPostExecute() {
        // Notify the adapter the new elements added
        GridViewCellAdapter adapter = (GridViewCellAdapter) mGridView.getAdapter();
        adapter.notifyDataSetChanged();

        mMainActivity.hideLoadingIndicator();

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
        if (!call.isCanceled()) {
            this.onPostExecute();

            // TODO: Check connectivity
        }
    }

    /**
     * Abort service handler requests (loader and retrofit call)
     */
    public void forceStop() {
        if (mRequestCall != null) {
            mRequestCall.cancel();
        }
    }
}