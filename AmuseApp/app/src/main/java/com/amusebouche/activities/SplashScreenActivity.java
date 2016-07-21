package com.amusebouche.activities;

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

import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.data.Recipe;

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

    private void goToMainView() {
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
            Integer numberOfRecipes = mDatabaseHelper.countRecipes();
            Log.d("INFO", "Number of recipes = " + numberOfRecipes);
            if (numberOfRecipes == 0) {
                Log.d("INFO", "Initialize example data");
                mDatabaseHelper.initializeExampleData();
            }

            mRecipes = mDatabaseHelper.getRecipes(mLimitPerPage, mLimitPerPage*mCurrentPage, null);

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

            goToMainView();
        }
    }
}