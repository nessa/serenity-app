package com.amusebouche.amuseapp;

import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
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
}