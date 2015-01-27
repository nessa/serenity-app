package com.amusebouche.amuseapp;

import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int PROFILE = 0, RECIPES = 1;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment initialFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

        // Set up the drawer
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getActionBar().setDisplayHomeAsUpEnabled(true);
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
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().add(R.id.container, fragment)
                    .addToBackStack("fragBack").commit();
            /*
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment)
                    .commitAllowingStateLoss();*/
            //restoreActionBar();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            //invalidateOptionsMenu(MainActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");

        if (getFragmentManager().findFragmentByTag("detailBack") != null){/* && initialFragment != null) {
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.addTransition(new ChangeImageTransform());
            transitionSet.addTransition(new ChangeBounds());
            transitionSet.setDuration(300);

            Fade fade = new Fade();
            fade.setStartDelay(300);
            initialFragment.setEnterTransition(fade);

            // Get main activity from context
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, initialFragment)
                    .addToBackStack("detailBack")
                    .commit();*/
        } else {
            super.onBackPressed();
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
            Fragment frag = getFragmentManager().findFragmentByTag("fragBack");
            FragmentTransaction transac = getFragmentManager().beginTransaction().remove(frag);
            transac.commit();
        }

    }
}