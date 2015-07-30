package com.amusebouche.amuseapp;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amusebouche.data.Recipe;

import java.util.ArrayList;


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
public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int PROFILE = 0, RECIPES = 1;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment initialFragment;
    private ArrayList<Recipe> mRecipes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        mRecipes = i.getParcelableArrayListExtra("recipes");

        // Set up the drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Set up action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
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

        FragmentManager fm = getFragmentManager();

        // The first fragment doesn't count (list fragment)
        if (fm.getBackStackEntryCount() > 1) {
            Log.i("MAIN", "popping backstack");
            fm.popBackStack();
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();

                            // Close app
                            Log.d("INFO", "FINISH");
                            finish();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.exit_message))
                    .setPositiveButton(getString(R.string.YES), dialogClickListener)
                    .setNegativeButton(getString(R.string.NO), dialogClickListener)
                    .show();
        }
    }

    /* Method needed to change the action bar button by using the navigation drawer fragment. */
    public void setDrawerIndicatorEnabled(boolean v) {
        mNavigationDrawerFragment.setDrawerIndicatorEnabled(v);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                search();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList getRecipes() {
        return mRecipes;
    }

    /**
     * Search will call to another (lateral) fragment.
     * TODO: Implement this method.
     */
    public void search() {
        Log.v("INFO", "Search clicked");
    }

}