package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amusebouche.data.Comment;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Recipe detail fourth tab fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of detail activity.
 * It's used inside a list of tabs.
 * It contains and shows the recipe's comments.
 *
 * Related layouts:
 * - Content: fragment_detail_fourth_tab.xml
 */
public class RecipeDetailFourthTabFragment extends Fragment {

    // Data variables
    private Recipe mRecipe;
    private ArrayList<Comment> mComments;

    // UI variables
    private ScrollView mScroll;
    private LinearLayout mCommentsView;
    private ProgressBar mLoadingIndicator;

    // Service variables
    ServiceHandler mServiceHandler;

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
     *
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
     *
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
    }


    /**
     * Called when the Fragment is no longer started.
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop()");
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be
     * reconstructed in a new instance of its process is restarted.
     *
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the fragment is no longer attached to its activity. Called after onDestroy.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * onCreate and onActivityCreated, onViewStateRestored, onStart().
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment,
     * @param container          This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If this fragment is being re-constructed from a previous saved
     *                           state as given here.
     * @return Return the View for the this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");

        // Get recipe from activity
        DetailActivity x = (DetailActivity) getActivity();
        mRecipe = x.getRecipe();

        // Get layouts
        RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_detail_fourth_tab,
                container, false);

        mScroll = (ScrollView) mLayout.findViewById(R.id.scroll);
        mLoadingIndicator = (ProgressBar) mLayout.findViewById(R.id.loading_indicator);
        mCommentsView = (LinearLayout) mLayout.findViewById(R.id.comments);

        // Get comments if needed
        if (mRecipe.getId() != null) {
            new GetComments().execute();
        }

        return mLayout;
    }

    private void showComments() {

        if (mComments != null) {
            for (int c = 0; c < mComments.size(); c++) {
                LinearLayout commentLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(
                    R.layout.item_detail_comment, mScroll, false);

                TextView userTextView = (TextView) commentLayout.findViewById(R.id.user);
                TextView timestampTextView = (TextView) commentLayout.findViewById(R.id.timestamp);
                TextView commentTextView = (TextView) commentLayout.findViewById(R.id.comment);

                userTextView.setText(mComments.get(c).getUser());
                timestampTextView.setText(mComments.get(c).getTimestamp().toString());
                commentTextView.setText(mComments.get(c).getComment());

                mCommentsView.addView(commentLayout);
            }
        }

        mLoadingIndicator.setVisibility(View.GONE);
        mScroll.setVisibility(View.VISIBLE);
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetComments extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mScroll.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... result) {
            if(!isCancelled()) {

                // Create service handler class instance
                mServiceHandler = new ServiceHandler(getActivity(),
                        getActivity().getPreferences(Context.MODE_PRIVATE));

                // Check internet connection
                if (mServiceHandler.checkInternetConnection()) {

                    // Make a request to url and getting response
                    String jsonStr = mServiceHandler.makeServiceCall(
                            "comments/?recipe=" + mRecipe.getId(),
                            ServiceHandler.GET);

                    if (jsonStr != null) {
                        try {
                            JSONObject jObject = new JSONObject(jsonStr);
                            JSONArray results = jObject.getJSONArray("results");

                            mComments = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                Comment c = new Comment(results.getJSONObject(i));
                                mComments.add(c);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            showComments();
        }
    }

}
