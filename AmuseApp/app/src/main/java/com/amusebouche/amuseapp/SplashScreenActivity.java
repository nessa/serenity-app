package com.amusebouche.amuseapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amusebouche.data.DatabaseHelper;
import com.amusebouche.data.Recipe;
import com.amusebouche.services.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends Activity {
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private DatabaseHelper mDatabaseHelper;
    private Boolean mOffline = true;
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences mSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Gets the database helper to access the database for the application
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        // Set paginate variables
        mCurrentPage = 0;
        // TODO: Get this from preferences??
        mLimitPerPage = 10;

        setContentView(R.layout.splash_screen);

        // Get languages from shared preferences
        final String languages = mSharedPreferences.getString(
                getString(R.string.preference_recipes_languages), "");

        // If there are no setted languages, ask for them
        if (languages.length() <= 0) {
            Dialog languagesDialog = new LanguagesDialog(this, mSharedPreferences);

            languagesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    new GetRecipes().execute();
                }
            });

            languagesDialog.show();
        } else {
            new GetRecipes().execute();
        }
    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetRecipes extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

            mTextView = (TextView) findViewById(R.id.text);
            mTextView.setText(getString(R.string.splash_screen_loading_recipes_message));
        }

        @Override
        protected Void doInBackground(Void... result) {
            if (mOffline) {
                Integer numberOfRecipes = mDatabaseHelper.countRecipes();
                Log.d("INFO", "Number of recipes = " + numberOfRecipes);
                if (numberOfRecipes == 0) {
                    Log.d("INFO", "Initialize example data");
                    mDatabaseHelper.initializeExampleData();
                }

                mRecipes = mDatabaseHelper.getRecipes(mLimitPerPage, mLimitPerPage*mCurrentPage, null);
            } else {
                // Create service handler class instance
                ServiceHandler sh = new ServiceHandler(getApplicationContext());

                // Check internet connection
                if (sh.checkInternetConnection()) {

                    // Make a request to url and getting response
                    String jsonStr = sh.makeServiceCall("recipes/", ServiceHandler.GET);

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
                    Log.d("INFO", "ELSE");
                    // TODO: Get database recipes??
                }
            }

            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Close the progress dialog
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);

            // Start the next activity
            Intent mainIntent = new Intent().setClass(
                    SplashScreenActivity.this, MainActivity.class);
            mainIntent.putExtra("recipes", mRecipes);
            mainIntent.putExtra("current_page", mCurrentPage);
            mainIntent.putExtra("limit", mLimitPerPage);
            startActivity(mainIntent);

            // Close the activity so the user won't be able to go back to this
            // activity pressing Back button
            finish();
        }
    }
}