package com.amusebouche.amuseapp;

import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.amusebouche.services.ServiceHandler;
import com.amusebouche.ui.FloatingActionButton;
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
 * - Menu: menu_recipe_list.xml
 * - Content: fragment_recipe_list.xml
 */
public class RecipeListFragment extends Fragment {
    private RelativeLayout mLayout;
    private ProgressDialog infoDialog;
    private GridView mGridView;
    private ArrayList<Recipe> mRecipes;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calling async task to get json
        if (mRecipes == null) {
            new GetRecipes().execute();
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

        mGridView = (GridView) mLayout.findViewById(R.id.gridview);
        mGridView.setAdapter(new GridviewCellAdapter(getActivity(), screen_width));

        // Add FAB programatically
        float scale = getResources().getDisplayMetrics().density;
        int addButtonSize = FloatingActionButton.convertToPixels(72, scale);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(addButtonSize,
            addButtonSize);

        FloatingActionButton addButton = new FloatingActionButton(this.getActivity());
        addButton.setFloatingActionButtonColor(getResources().getColor(R.color.theme_default_accent));
        addButton.setFloatingActionButtonDrawable(getResources()
            .getDrawable(R.drawable.ic_add_white_48dp));

        params.bottomMargin = FloatingActionButton.convertToPixels(20, scale);
        params.rightMargin = FloatingActionButton.convertToPixels(20, scale);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        addButton.setLayoutParams(params);
        mLayout.addView(addButton);

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        //outState.putCharSequence("text", mEditText.getText());
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
    private class GetRecipes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show progress dialog
            MainActivity x = (MainActivity) getActivity();
            infoDialog = new ProgressDialog(x);
            infoDialog.setMessage(x.getApplicationContext().getResources()
                    .getString(R.string.please_wait_message));
            infoDialog.setCancelable(false);
            infoDialog.show();
        }

        @Override
        protected Void doInBackground(Void... result) {
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

                        mRecipes = new ArrayList<>();

                        for (int i = 0; i < results.length(); i++) {
                            mRecipes.add(new Recipe(results.getJSONObject(i)));
                        }

                        GridviewCellAdapter adapter = (GridviewCellAdapter) mGridView.getAdapter();
                        adapter.setRecipes(mRecipes);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
            } else {
                Log.d("INFO", "There's no connection");

                // TODO: Get database recipes??
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (infoDialog.isShowing())
                infoDialog.dismiss();

            // Update parsed JSON data (result?) into GridView
            GridviewCellAdapter adapter = (GridviewCellAdapter) mGridView.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }
}