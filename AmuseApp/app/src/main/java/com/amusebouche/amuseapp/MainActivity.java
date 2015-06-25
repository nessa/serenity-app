package com.amusebouche.amuseapp;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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

    /**
     * Search will call to another (lateral) fragment.
     * TODO: Implement this method.
     */
    public void search() {
        Log.v("INFO", "Search clicked");
    }

}