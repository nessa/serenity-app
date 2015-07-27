package com.amusebouche.amuseapp;


import android.app.Activity;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Detail activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Detail app activity (recipe detail).
 * It has:
 * - The present fragment that is active.
 *
 * Related layouts:
 * - Content: activity_detail.xml
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DETAIL", "DETAIL ACTIVITY ON CREATE");

        setContentView(R.layout.activity_detail);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("PRUEBA");
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
    }

    /*
    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("DETAIL", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("DETAIL", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}