package com.amusebouche.amuseapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.data.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Main activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Main app activity.
 * It has:
 * - Two navigation drawers:
 *   + A left lateral menu.
 *   + A right search view.
 * - The present fragment that is active (changed by the left navigation drawer):
 *   + User fragment
 *   + List fragment (with 3 modes)
 *   + Setting fragment
 *   + Information fragment
 *
 * Related layouts:
 * - Menu: menu_recipe_list.xml
 * - Content: activity_main.xml
 * - Left drawer: drawer_left_menu.xml
 * - Right drawer: drawer_right_menu.xml
 */
public class MainActivity extends ActionBarActivity {

    // MENU
    private static final int PROFILE = 0;
    private static final int NEW_RECIPES = 1;
    private static final int DOWNLOADED_RECIPES = 2;
    private static final int MY_RECIPES = 3;
    private static final int SETTINGS = 4;
    private static final int INFO = 5;

    // MODES
    private static int mRecipeListMode;
    public static final int NEW_RECIPES_MODE = 1;
    public static final int DOWNLOADED_RECIPES_MODE = 2;
    public static final int MY_RECIPES_MODE = 3;

    // SAVED DATA KEYS
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";
    private static final String PREVIOUS_TOTAL_KEY = "previous_total";
    private static final String SELECTED_DRAWER_VIEW_KEY = "selected_drawer_view";


    // Data variables
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;
    private Integer mPreviousTotal;

    // UI variables
    private Fragment mLastFragment;

    // Drawers
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mLeftDrawerView;
    private LinearLayout mRightDrawerView;
    private ListView mLeftDrawerList;
    private LeftMenuAdapter mLeftMenuAdapter;
    private int mCurrentSelectedPosition = 1;

    private View.OnClickListener mCollapseClickListener;

    private ArrayList<Pair<String, ArrayList<String>>> mFilterParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        // Get data from previous activity
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipes = i.getParcelableArrayListExtra(PARCELABLE_RECIPES_KEY);
            mCurrentPage = i.getIntExtra(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = i.getIntExtra(LIMIT_PER_PAGE_KEY, 0);
            mPreviousTotal = i.getIntExtra(PREVIOUS_TOTAL_KEY, 0);
        } else {
            mRecipes = savedInstanceState.getParcelableArrayList(PARCELABLE_RECIPES_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = savedInstanceState.getInt(LIMIT_PER_PAGE_KEY, 0);
            mPreviousTotal = savedInstanceState.getInt(PREVIOUS_TOTAL_KEY, 0);
            mCurrentSelectedPosition = savedInstanceState.getInt(SELECTED_DRAWER_VIEW_KEY, 1);
        }

        // Set up action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (mDrawerLayout == null || mLeftDrawerView == null || mRightDrawerView == null || mDrawerToggle == null) {
            // Configure navigation drawer
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


            // Set left menu view
            mLeftDrawerView = (LinearLayout) findViewById(R.id.left_menu);
            mLeftDrawerList = (ListView) findViewById(R.id.left_menu_list);
            mLeftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItemInLeftMenu(position);
                }
            });
            mLeftDrawerList.setDivider(null);
            mLeftMenuAdapter = mLeftMenuAdapter == null ?
                    new LeftMenuAdapter(this) : mLeftMenuAdapter;
            mLeftDrawerList.setAdapter(mLeftMenuAdapter);

            selectItemInLeftMenu(mCurrentSelectedPosition);


            // Set right search view
            mRightDrawerView = (LinearLayout) findViewById(R.id.right_search);
            setRightDrawer();


            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.accept, R.string.cancel) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View drawerView) {
                    if (drawerView.equals(mLeftDrawerView)) {
                        // Call to onPrepareOptionsMenu()
                        supportInvalidateOptionsMenu();
                        mDrawerToggle.syncState();
                    }
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    if (drawerView.equals(mLeftDrawerView)) {
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

    private void setRightDrawer() {
        if (mCollapseClickListener == null) {
            mCollapseClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get collapse image by its tag
                    ImageView collapseFoldImage = (ImageView) view.findViewWithTag(
                            getString(R.string.search_view_collapse_fold_image));


                    // Get collapsible view by its tag through this view parent
                    LinearLayout parent = (LinearLayout) view.getParent();

                    final LinearLayout collapsibleView = (LinearLayout) parent.findViewWithTag(
                            getString(R.string.search_view_collapsible_view));

                    // Set collapse image depending on collapsible view visibility
                    collapseFoldImage.setBackgroundResource(collapsibleView.isShown() ?
                            R.drawable.ic_keyboard_arrow_up_black_48px : R.drawable.ic_keyboard_arrow_down_black_48px);

                    // Animations to show or hide
                    if (collapsibleView.isShown()) {
                        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);

                        slide_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                collapsibleView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });

                        collapsibleView.startAnimation(slide_up);
                    } else {
                        collapsibleView.setVisibility(View.VISIBLE);

                        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);
                        collapsibleView.startAnimation(slide_down);
                    }
                }
            };
        }

        resetFilterParams();

        // Title filter
        final TextView titleFilterTextView = (TextView) mRightDrawerView.findViewById(R.id.title_filter);
        Button clearTitleFilterButton = (Button) mRightDrawerView.findViewById(R.id.clear_title_filter);

        clearTitleFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleFilterTextView.setText("");
            }
        });

        // Get clickable views
        final RelativeLayout ingredientsCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.ingredients_collapse);
        final RelativeLayout allergensCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.allergens_collapse);

        // Get collapsible views
        final LinearLayout ingredientsCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.ingredients_collapsible_view);
        final LinearLayout allergensCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.allergens_collapsible_view);

        // Hide collapsible views
        ingredientsCollapsibleView.setVisibility(View.GONE);
        allergensCollapsibleView.setVisibility(View.GONE);

        // Set listeners on clickable views
        ingredientsCollapse.setOnClickListener(mCollapseClickListener);
        allergensCollapse.setOnClickListener(mCollapseClickListener);


        final ImageView ingredientsCollapseFoldImage = (ImageView) mRightDrawerView.findViewById(R.id.ingredients_collapse_fold_image);




        // Get buttons
        Button cancelButton = (Button) mRightDrawerView.findViewById(R.id.cancel);
        Button acceptButton = (Button) mRightDrawerView.findViewById(R.id.accept);

        // Buttons listeners
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(mRightDrawerView);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleFilterTextView.getText().toString().length() > 0) {
                    addFilterToFilterParams(getString(R.string.API_PARAM_TITLE),
                            titleFilterTextView.getText().toString(), true);
                }

                mDrawerLayout.closeDrawer(mRightDrawerView);

                // Reset list fragment
                selectItemInLeftMenu(mCurrentSelectedPosition);
            }
        });

    }

    private void resetFilterParams() {
        // Reset filter params
        mFilterParams = new ArrayList<>();
    }

    private void addFilterToFilterParams(String key, String value, boolean onlyOne) {
        int found = -1;

        if (mFilterParams != null) {
            for (int i = 0; i < mFilterParams.size(); i++) {

                if (key.equals(mFilterParams.get(i).first)) {
                    found = i;
                }
            }
        }

        if (found > -1) {
            if (onlyOne) {
                mFilterParams.get(found).second.clear();
            }

            mFilterParams.get(found).second.add(value);
        } else {
            mFilterParams.add(Pair.create(key, new ArrayList<>(Collections.singletonList(value))));
        }
    }

    public ArrayList<Pair<String, ArrayList<String>>> getFilterParams() {
        return mFilterParams;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mRecipes = data.getParcelableArrayListExtra(PARCELABLE_RECIPES_KEY);
            mCurrentPage = data.getIntExtra(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = data.getIntExtra(LIMIT_PER_PAGE_KEY, 0);
            mPreviousTotal = data.getIntExtra(PREVIOUS_TOTAL_KEY, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        outState.putParcelableArrayList(PARCELABLE_RECIPES_KEY, mRecipes);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
        outState.putInt(LIMIT_PER_PAGE_KEY, mLimitPerPage);
        outState.putInt(PREVIOUS_TOTAL_KEY, 0);
        outState.putInt(SELECTED_DRAWER_VIEW_KEY, mCurrentSelectedPosition);
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
     * We inflate the menu options in every fragment, but the selection
     * effect it's done here.
     */
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


        // If it's open any drawer, close it
        if (mDrawerLayout.isDrawerOpen(mLeftDrawerView)) {
            mDrawerLayout.closeDrawer(mLeftDrawerView);
        } else {
            if (mDrawerLayout.isDrawerOpen(mRightDrawerView)) {
                mDrawerLayout.closeDrawer(mRightDrawerView);
            } else {
                // Elsewhere show an exit dialog
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();

                                // Close app
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());

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
    }


    // NAVIGATION DRAWER METHODS

    public void selectItemInLeftMenu(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null && mLeftDrawerView != null) {
            mLeftDrawerList.setItemChecked(mCurrentSelectedPosition, true);
            mDrawerLayout.closeDrawer(mLeftDrawerView);
        }

        // Prevent fragment from getting stuck
        if (mLastFragment instanceof RecipeListFragment) {
            ((RecipeListFragment) mLastFragment).forceStop();
        }

        // Update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case PROFILE:
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_login));
                fragment = new UserFragment();
                break;
            default:
            case NEW_RECIPES:
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_new_recipes));
                this.resetRecipesAndSetMode(NEW_RECIPES_MODE);
                fragment = new RecipeListFragment();
                break;
            case DOWNLOADED_RECIPES:
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_downloaded_recipes));
                this.resetRecipesAndSetMode(DOWNLOADED_RECIPES_MODE);
                fragment = new RecipeListFragment();
                break;
            case MY_RECIPES:
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_my_recipes));
                this.resetRecipesAndSetMode(MY_RECIPES_MODE);
                fragment = new RecipeListFragment();
                break;
            case SETTINGS:
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_settings));
                fragment = new SettingsFragment();
                break;
            case INFO:
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_info));
                fragment = new InformationFragment();
                break;
        }

        if (!isFinishing()) {
            if (mLastFragment == null) {
                getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
            } else {
                getFragmentManager().beginTransaction().remove(mLastFragment)
                        .add(R.id.container, fragment).commit();
            }

            mLastFragment = fragment;
        }
    }

    private void resetRecipesAndSetMode(int mode) {
        mRecipes.clear();
        mCurrentPage = 0;
        mLimitPerPage = 10;
        mPreviousTotal = 0;

        this.setMode(mode);
    }

    // GETTERS

    public ArrayList<Recipe> getRecipes() {
        return mRecipes;
    }

    public int getMode() {
        return mRecipeListMode;
    }

    public Integer getCurrentPage() {
        return mCurrentPage;
    }

    public Integer getLimitPerPage() {
        return mLimitPerPage;
    }

    public Integer getPreviousTotal() {
        return mPreviousTotal;
    }

    // SETTERS

    public void setMode(int mode) {
        mRecipeListMode = mode;
    }

    public void setCurrentPage(Integer currentPage) {
        mCurrentPage = currentPage;
    }

    public void setPreviousTotal(Integer previousTotal) {
        mPreviousTotal = previousTotal;
    }


}