package com.amusebouche.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.amusebouche.fragments.RecipeDetailThirdTabFragment;

import java.io.FileInputStream;


/**
 * Detail activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Detail app activity (recipe detail).
 * It has:
 * - The present fragment that is active.
 * - A tab widget definition with its tabs.
 *
 * Related layouts:
 * - Menu: menu_recipe_detail.xml
 * - Content: activity_detail.xml
 */
public class DetailActivity extends ActionBarActivity {

    // Tab identifiers
    private static final String TAB_1 = "first_tab";
    private static final String TAB_2 = "second_tab";
    private static final String TAB_3 = "third_tab";
    private static final String TAB_4 = "fourth_tab";

    // Saved data keys
    private static final String INTENT_KEY_RECIPE = "recipe";
    private static final String INTENT_KEY_TAB = "tab";
    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";

    // Data variables
    private Recipe mRecipe;
    private Bitmap mMainImage;
    private TabHost mTabs;
    private RatingBar mRatingBar;
    private RelativeLayout mFadeViews;

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

        // Get saved data
        String presentTab = TAB_1;
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipe = i.getParcelableExtra(INTENT_KEY_RECIPE);
        } else {
            mRecipe = savedInstanceState.getParcelable(INTENT_KEY_RECIPE);
            presentTab = savedInstanceState.getString(INTENT_KEY_TAB);
        }

        // Get image bitmap from file
        String FILENAME = "presentRecipeImage.png";
        try {
            FileInputStream is = this.openFileInput(FILENAME);
            mMainImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_detail);
        getSupportActionBar().setElevation(0);

        // Set tabs
        mTabs = (TabHost)findViewById(android.R.id.tabhost);
        mTabs.setup();

        // First tab: information
        TabHost.TabSpec spec = mTabs.newTabSpec(TAB_1);
        spec.setContent(R.id.tab1);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_description_32dp));
        mTabs.addTab(spec);

        // Second tab: ingredients
        spec = mTabs.newTabSpec(TAB_2);
        spec.setContent(R.id.tab2);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_ingredients_32dp));
        mTabs.addTab(spec);

        // Third tab: directions
        spec = mTabs.newTabSpec(TAB_3);
        spec.setContent(R.id.tab3);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_cook_32dp));
        mTabs.addTab(spec);

        // Fourth tab: comments (TODO)
        spec = mTabs.newTabSpec(TAB_4);
        spec.setContent(R.id.tab4);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_comments_32dp));
        mTabs.addTab(spec);

        // Show first tab at the beginning
        mTabs.setCurrentTabByTag(presentTab);
        resetTabColors();
        resetBarTitle(presentTab);

        mTabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mTabs.setCurrentTabByTag(tabId);
                resetTabColors();
                resetBarTitle(tabId);
            }
        });

        // Set rating bar colors
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.theme_default_primary),
                PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Change the tab widget appearance.
     * Set different styles for basic and selected tabs.
     */
    private void resetTabColors() {
        for (int i = 0; i < mTabs.getTabWidget().getChildCount(); i++) {
            // Unselected ones
            mTabs.getTabWidget().getChildAt(i)
                    .setBackgroundColor(getResources().getColor(android.R.color.white));

            ImageView iv = (ImageView) mTabs.getTabWidget().getChildAt(i).findViewById(android.R.id.icon);
            iv.setColorFilter(getResources().getColor(R.color.theme_default_primary), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // Selected ones
        mTabs.getTabWidget().getChildAt(mTabs.getCurrentTab())
                .setBackgroundColor(getResources().getColor(R.color.theme_default_primary));
        ImageView iv = (ImageView) mTabs.getCurrentTabView().findViewById(android.R.id.icon);
        iv.setColorFilter(getResources()
                .getColor(android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    /**
     * Change action bar title every time we select a new tab.
     * @param tab Selected tab
     */
    private void resetBarTitle(String tab) {
        // Set
        switch (tab) {
            case TAB_2:
                getSupportActionBar().setTitle(getString(R.string.detail_ingredients_label));
                break;
            case TAB_3:
                getSupportActionBar().setTitle(getString(R.string.detail_directions_label));
                break;
            case TAB_4:
                getSupportActionBar().setTitle(getString(R.string.detail_comments_label));
                break;
            default:
            case TAB_1:
                getSupportActionBar().setTitle(getString(R.string.detail_information_label));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("INFO", "ON START");

        // Set image
        ImageView mRecipeImage = (ImageView) findViewById(R.id.recipe_image);
        mRecipeImage.setImageBitmap(mMainImage);

        // Set name
        TextView mRecipeName = (TextView) findViewById(R.id.recipe_name);
        mRecipeName.setText(mRecipe.getTitle());

        // Set user
        TextView mRecipeOwner = (TextView) findViewById(R.id.recipe_owner);
        mRecipeOwner.setText(mRecipe.getOwner());

        // Set rating
        float rating = 0;
        if (mRecipe.getUsersRating() > 0) {
            rating = mRecipe.getTotalRating() / mRecipe.getUsersRating();
        }
        mRatingBar.setRating(rating);

        // Set number of users
        TextView mRecipeNumberUsersRating = (TextView) findViewById(R.id.recipe_number_users_rating);
        mRecipeNumberUsersRating.setText(String.format("(%d %s)", mRecipe.getUsersRating(),
                getString(R.string.detail_users)));

        // Fade in data
        mFadeViews = (RelativeLayout) findViewById(R.id.fade_views);
        final RelativeLayout finalFadeViews = mFadeViews;

        mFadeViews.post(new Runnable() {
            @Override
            public void run() {
                finalFadeViews.setVisibility(View.VISIBLE);
                finalFadeViews.setAlpha(0);
                finalFadeViews.animate()
                        .alpha(1f)
                        .setDuration(2000)
                        .setListener(null);
            }
        } );
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so
     * the state can be restored in onCreate method.
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putParcelable(INTENT_KEY_RECIPE, mRecipe);
        outState.putString(INTENT_KEY_TAB, mTabs.getCurrentTabTag());
    }


    // UI METHODS

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");

        boolean goBack = true;
        if (mTabs.getCurrentTabTag().equals(TAB_3)) {
            RecipeDetailThirdTabFragment frag = (RecipeDetailThirdTabFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment3);

            if (frag.isInOngoingMode()) {
                frag.exitFromOngoingMode();
                goBack = false;
            }
        }

        if (goBack) {
            // Fade out title and view before go back
            Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
            fadeOutAnimation.setDuration(1000);

            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    DetailActivity.super.onBackPressed();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mFadeViews.startAnimation(fadeOutAnimation);
        }
    }


    // MENU METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail, menu);

        MenuItem downloadItem = menu.findItem(R.id.action_download);
        downloadItem.setVisible(mRecipe.getIsOnline());

        // TODO Fix this! The database id is not set
        MenuItem editItem = menu.findItem(R.id.action_edit);
        editItem.setVisible(!mRecipe.getDatabaseId().equals(""));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                editRecipe();
                return true;
            case R.id.action_save:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // GETTERS

    /**
     * Get recipe method to share it with its fragments.
     * @return Recipe object
     */
    public Recipe getRecipe() { return mRecipe; }


    // FUNCTIONALITY METHODS

    public void editRecipe() {
        Intent i = new Intent(this, EditionActivity.class);
        i.putExtra(INTENT_KEY_RECIPE, mRecipe);
        startActivity(i);
    }

}