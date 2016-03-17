package com.amusebouche.amuseapp;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.amusebouche.data.Recipe;

import java.util.ArrayList;


/**
 * Add activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Add app activity (recipe creation).
 *
 * Related layouts:
 * - Content: activity_add.xml
 */
public class AddActivity extends ActionBarActivity {

    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";


    // Data variables
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;

    // LIFECYCLE METHODS

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * @param savedInstanceState Data supplied when the activity is being re-initialized
     *                           after previously being shut down.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        // Get data from previous activity
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipes = i.getParcelableArrayListExtra(PARCELABLE_RECIPES_KEY);
            mCurrentPage = i.getIntExtra(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = i.getIntExtra(LIMIT_PER_PAGE_KEY, 0);
        } else {
            mRecipes = savedInstanceState.getParcelableArrayList(PARCELABLE_RECIPES_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = savedInstanceState.getInt(LIMIT_PER_PAGE_KEY, 0);
        }

        overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        setContentView(R.layout.activity_add);
    }
    @Override
    protected void onPause() {
        // Whenever this activity is paused (i.e. looses focus because another activity is started etc)
        // Override how this activity is animated out of view
        // The new activity is kept still and this activity is pushed out to the left
        overridePendingTransition(R.anim.hold, R.anim.push_out_to_left);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so
     * the state can be restored in onCreate method.
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }



    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveData();
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Send main activity its proper data again
     */
    private void saveData() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PARCELABLE_RECIPES_KEY, mRecipes);
        intent.putExtra(CURRENT_PAGE_KEY, mCurrentPage);
        intent.putExtra(LIMIT_PER_PAGE_KEY, mLimitPerPage);

        setResult(RESULT_OK, intent);
    }

}