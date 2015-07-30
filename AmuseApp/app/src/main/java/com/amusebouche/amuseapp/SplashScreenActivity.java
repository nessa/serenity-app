package com.amusebouche.amuseapp;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.amusebouche.data.DatabaseHelper;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.ServiceHandler;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends Activity {
    private ProgressBar mProgressBar;
    private DatabaseHelper mDatabaseHelper;
    private Boolean mOffline = true;
    private ArrayList<Recipe> mRecipes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Gets the database helper to access the database for the application
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        setContentView(R.layout.splash_screen);

        new GetRecipes().execute();
    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetRecipes extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        }

        @Override
        protected Void doInBackground(Void... result) {
            if (mOffline) {
                mDatabaseHelper.initializeExampleData();
                mRecipes = mDatabaseHelper.getRecipes();
            } else {
                // Create service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Check internet connection
                if (sh.checkInternetConnection(getApplicationContext())) {

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                } else {
                    // TODO: Get database recipes??
                }
            }

            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //set the current progress of the progress dialog
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Close the progress dialog
            mProgressBar.setVisibility(View.GONE);

            // Start the next activity
            Intent mainIntent = new Intent().setClass(
                    SplashScreenActivity.this, MainActivity.class);
            mainIntent.putExtra("recipes", mRecipes);
            startActivity(mainIntent);

            // Close the activity so the user won't able to go back this
            // activity pressing Back button
            finish();
        }
    }
}