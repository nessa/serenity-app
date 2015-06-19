package com.amusebouche.amuseapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import android.os.AsyncTask;
import android.app.ProgressDialog;

import com.amusebouche.services.ServiceHandler;

/**
 * Main activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Main app activity.
 * It has:
 * - The navigation drawer (lateral menu).
 * - The present fragment that is active (changed by the navigation drawer).
 *
 * Related layouts:
 * - Content: activity_main.xml
 */
public class MainActivity extends Activity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int PROFILE = 0, RECIPES = 1;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment initialFragment;

    private ProgressDialog infoDialog;

    // Recipes JSONArray
    JSONArray recipes = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Calling async task to get json
        new GetRecipes().execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Update the main content by replacing fragments
        Fragment fragment;
        String tab = "";
        switch (position) {
            /*
            case PROFILE:
                fragment = new RecipeListFragment();
                break;*/
            default:
            case RECIPES:
                tab = "Recipes";
                initialFragment = new RecipeListFragment();
                fragment = initialFragment;
                break;
        }
        if (!isFinishing()) {
            // TODO: Check this code.
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().add(R.id.container, fragment)
                    .addToBackStack("fragBack").commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");

        if (getFragmentManager().findFragmentByTag("detailBack") != null){
            // TODO: Something?
        } else {
            super.onBackPressed();
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
            /*
            Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
            Fragment frag = getFragmentManager().findFragmentByTag("fragBack");
            FragmentTransaction transac = getFragmentManager().beginTransaction().remove(frag);
            transac.commit();*/
        }
    }

    /* Method needed to change the action bar button by using the navigation drawer fragment. */
    public void setDrawerIndicatorEnabled(boolean v) {
        mNavigationDrawerFragment.setDrawerIndicatorEnabled(v);
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetRecipes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            infoDialog = new ProgressDialog(MainActivity.this);
            infoDialog.setMessage("Please wait...");
            infoDialog.setCancelable(false);
            infoDialog.show();

        }

        @Override
        protected Void doInBackground(Void) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("recipes/", ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    /*
                     * TODO: Convert JSON into object
                    Gson gson = new Gson();
                    JSONConverter obj = gson.fromJson(json, JSONConverter.class);
                     */
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (infoDialog.isShowing())
                infoDialog.dismiss();

            /**
             * TODO: Updating parsed JSON data (result?) into GridView
             */
        }

    }

}