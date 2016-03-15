package com.amusebouche.amuseapp;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amusebouche.data.Recipe;

import java.util.ArrayList;


/**
 * Main activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Main app activity.
 * It has:
 * - Two navigation drawers:
 *   + A left lateral menu.
 *   + A right search view.
 * - The present fragment that is active (changed by the left navigation drawer).
 *
 * Related layouts:
 * - Menu: menu_recipe_list.xml
 * - Content: activity_main.xml
 * - Left drawer: drawer_left_menu.xml
 * - Right drawer: drawer_right_menu.xml
 */
public class MainActivity extends ActionBarActivity {

    private static final int PROFILE = 0, RECIPES = 1;
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";


    // Data variables
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;

    // UI variables
    private Fragment mInitialFragment;

    // Left drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLeftDrawerView;
    private LeftMenuAdapter mLeftMenuAdapter;
    private int mCurrentSelectedPosition = 1;

    // Right drawer
    private LinearLayout mRightDrawerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get data from previous activity
        Intent i = getIntent();
        mRecipes = i.getParcelableArrayListExtra(PARCELABLE_RECIPES_KEY);
        mCurrentPage = i.getIntExtra(CURRENT_PAGE_KEY, 0);
        mLimitPerPage = i.getIntExtra(LIMIT_PER_PAGE_KEY, 0);

        // Set up action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDrawerLayout == null || mLeftDrawerView == null || mRightDrawerView == null || mDrawerToggle == null) {
            // Configure navigation drawer
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


            // Set left menu view
            mLeftDrawerView = (ListView) findViewById(R.id.left_menu);
            mLeftDrawerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItemInLeftMenu(position);
                }
            });
            mLeftDrawerView.setDivider(null);
            mLeftMenuAdapter = mLeftMenuAdapter == null ?
                    new LeftMenuAdapter(this) : mLeftMenuAdapter;
            mLeftDrawerView.setAdapter(mLeftMenuAdapter);

            selectItemInLeftMenu(mCurrentSelectedPosition);


            // Set right search view
            mRightDrawerView = (LinearLayout) findViewById(R.id.right_search);


            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.accept, R.string.cancel) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View drawerView) {
                    if (drawerView.equals(mLeftDrawerView)) {
                        getSupportActionBar().setTitle(getTitle());
                        // Call to onPrepareOptionsMenu()
                        supportInvalidateOptionsMenu();
                        mDrawerToggle.syncState();
                    }
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    if (drawerView.equals(mLeftDrawerView)) {
                        getSupportActionBar().setTitle(getString(R.string.app_name));
                        // Call to onPrepareOptionsMenu()
                        supportInvalidateOptionsMenu();
                        mDrawerToggle.syncState();
                    }
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    /* Avoid normal indicator glyph behaviour.
                     * This is to avoid glyph movement when opening the right drawer. */
                    //super.onDrawerSlide(drawerView, slideOffset);
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle); // Set the drawer toggle as the DrawerListener
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred
        mDrawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // MENU METHODS

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // If the nav drawer is open, hide action items related to the content view
        for (int i = 0; i< menu.size(); i++) {
            menu.getItem(i).setVisible(!mDrawerLayout.isDrawerOpen(mLeftDrawerView));
        }

        return super.onPrepareOptionsMenu(menu);
    }


    /**
     *  Inflate the menu items to use them in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                mDrawerToggle.onOptionsItemSelected(item);

                if (mDrawerLayout.isDrawerOpen(mLeftDrawerView)) {
                    mDrawerLayout.closeDrawer(mLeftDrawerView);
                } else {
                    if(mDrawerLayout.isDrawerOpen(mRightDrawerView)) {
                        mDrawerLayout.closeDrawer(mRightDrawerView);
                    }

                    mDrawerLayout.openDrawer(mLeftDrawerView);
                }

                return true;
            case R.id.action_search:
                mDrawerToggle.onOptionsItemSelected(item);

                if (mDrawerLayout.isDrawerOpen(mRightDrawerView)) {
                    mDrawerLayout.closeDrawer(mRightDrawerView);
                } else {
                    if (mDrawerLayout.isDrawerOpen(mLeftDrawerView)) {
                        mDrawerLayout.closeDrawer(mLeftDrawerView);
                    }

                    mDrawerLayout.openDrawer(mRightDrawerView);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // UI METHODS

    /**
     * Called when the activity has detected the user's press of the back key.
     */
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


    // NAVIGATION DRAWER METHODS

    public void selectItemInLeftMenu(int position) {
        mCurrentSelectedPosition = position;
        if (mLeftDrawerView != null) {
            mLeftDrawerView.setItemChecked(mCurrentSelectedPosition, true);
        }
        if (mDrawerLayout != null && mLeftDrawerView != null) {
            mDrawerLayout.closeDrawer(mLeftDrawerView);
        }

        // Update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            /*
            case PROFILE:
                fragment = new RecipeListFragment();
                break;*/
            default:
            case RECIPES:
                mInitialFragment = new RecipeListFragment();
                fragment = mInitialFragment;
                break;
        }

        if (!isFinishing()) {
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().add(R.id.container, fragment)
                    .addToBackStack("fragBack").commit();
        }
    }


    // GETTERS

    public ArrayList<Recipe> getRecipes() {
        return mRecipes;
    }

    public Integer getCurrentPage() {
        return mCurrentPage;
    }

    public Integer getLimitPerPage() {
        return mLimitPerPage;
    }
}