package com.amusebouche.activities;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.amusebouche.loaders.GetRecipeLoader;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.data.Recipe;
import com.amusebouche.fragments.RecipeDetailThirdTabFragment;
import com.amusebouche.services.RetrofitServiceGenerator;
import com.securepreferences.SecurePreferences;

import java.io.FileInputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    // The loader's unique id.
    private static final int LOADER_ID = 3;

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
    private Recipe mAPIRecipe;
    private Recipe mDatabaseRecipe;

    // UI
    private Bitmap mMainImage;
    private TabHost mTabs;
    private RatingBar mRatingBar;
    private RelativeLayout mFadeViews;

    // Preferences
    private SharedPreferences mUserPreferences;
    private String mUsername;
    private boolean isUserLoggedIn;

    // Behaviour variables
    private boolean mDownloadEnabled = false;

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

        // Check if user is logged in
        mUserPreferences = new SecurePreferences(this, "", "user_preferences.xml");
        String mAuthToken = mUserPreferences.getString(getString(R.string.preference_auth_token), "");
        mUsername = mUserPreferences.getString(getString(R.string.preference_username), "");
        isUserLoggedIn = !mAuthToken.equals("");

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

        // Define check recipes
        mAPIRecipe = null;
        mDatabaseRecipe = null;

        setContentView(R.layout.activity_detail);
        getSupportActionBar().setElevation(0);

        // Set tabs
        mTabs = (TabHost)findViewById(android.R.id.tabhost);
        mTabs.setup();

        // First tab: information
        TabHost.TabSpec spec = mTabs.newTabSpec(TAB_1);
        spec.setContent(R.id.tab1);
        spec.setIndicator("", getDrawable(R.drawable.ic_description_32dp));
        mTabs.addTab(spec);

        // Second tab: ingredients
        spec = mTabs.newTabSpec(TAB_2);
        spec.setContent(R.id.tab2);
        spec.setIndicator("", getDrawable(R.drawable.ic_ingredients_32dp));
        mTabs.addTab(spec);

        // Third tab: directions
        spec = mTabs.newTabSpec(TAB_3);
        spec.setContent(R.id.tab3);
        spec.setIndicator("", getDrawable(R.drawable.ic_cook_32dp));
        mTabs.addTab(spec);

        // Fourth tab: comments (TODO)
        spec = mTabs.newTabSpec(TAB_4);
        spec.setContent(R.id.tab4);
        spec.setIndicator("", getDrawable(R.drawable.ic_comments_32dp));
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

        // Get check recipes
        getCheckRecipes();

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
        });
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
        Log.d("DETAIL", "ON CREATE OPTIONS MENU");

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail, menu);

        /* Edit item is enabled when:
         * - Recipe is in database AND
         * - User is not set OR is set and it matches the logged username
         */
        MenuItem editItem = menu.findItem(R.id.action_edit);
        editItem.setVisible(!mRecipe.getDatabaseId().equals("") &&
                (mRecipe.getOwner().equals("") || mRecipe.getOwner().equals(mUsername)));

        /* Download item is enabled when:
         * - Recipe doesn't exist on database OR
         * - Recipe exist on database AND API_Recipe.updated_timestamp > DB_Recipe.updated_timestamp
         */
        MenuItem downloadItem = menu.findItem(R.id.action_download);
        downloadItem.setVisible(mDatabaseRecipe == null || (mDatabaseRecipe != null &&
                mAPIRecipe != null && mAPIRecipe.getUpdatedTimestamp().getTime() >
                mDatabaseRecipe.getUpdatedTimestamp().getTime()));

        /* TODO: Check this! If someone rates or comments the recipe, may change the updated
         * timestamp */
        /* Upload item is enabled when:
         * - Recipe doesn't exist on API
         */
        MenuItem uploadItem = menu.findItem(R.id.action_upload);
        uploadItem.setVisible(mAPIRecipe == null || (mAPIRecipe != null && mDatabaseRecipe != null &&
                mDatabaseRecipe.getUpdatedTimestamp().getTime() >
                        mAPIRecipe.getUpdatedTimestamp().getTime()));

        /* Rate item is enabled when:
         * - User is logged in
         */
        MenuItem rateItem = menu.findItem(R.id.action_rate);
        rateItem.setVisible(isUserLoggedIn);

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
            case R.id.action_download:
                downloadRecipe();
                return true;
            case R.id.action_upload:
                uploadRecipe();
                return true;
            case R.id.action_rate:
                rateRecipe();
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

    /**
     * Show edition view
     */
    public void editRecipe() {
        Intent i = new Intent(this, EditionActivity.class);
        i.putExtra(INTENT_KEY_RECIPE, mRecipe);
        startActivity(i);
    }

    /**
     * Download recipe and store it into the database
     */
    public void downloadRecipe() {
        Log.d("DETAIL", "DOWNLOAD");
        mDownloadEnabled = true;
    }

    /**
     * Upload recipe to the API
     */
    public void uploadRecipe() {
        Log.d("DETAIL", "UPLOAD");
    }

    /**
     * Open rate dialog
     */
    public void rateRecipe() {
        Log.d("DETAIL", "RATE");
    }

    /**
     * Loads recipes to check data: API and database recipes
     */
    private void getCheckRecipes() {
        if (mRecipe.getIsOnline()) {
            mAPIRecipe = mRecipe;

            Loader l = getLoaderManager().getLoader(LOADER_ID);
            if (l != null) {
                getLoaderManager().restartLoader(LOADER_ID, null, this);
            } else {
                getLoaderManager().initLoader(LOADER_ID, null, this);
            }
        } else {
            mDatabaseRecipe = mRecipe;

            if (!mRecipe.getId().equals("") && !mRecipe.getId().equals("0")) {
                AmuseAPI api = RetrofitServiceGenerator.createService(AmuseAPI.class);

                Call<ResponseBody> requestCall = api.getRecipe(mRecipe.getId());

                // Asynchronous call
                requestCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        onRecipesLoaded();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        onRecipesLoaded();
                    }

                });
            }
        }
    }

    /**
     * Reloads bar menu when all recipes are loaded
     */
    private void onRecipesLoaded() {
        openOptionsMenu();
    }

    // LOADER METHODS

    /**
     * Creates the loader
     * @param id Loader id
     * @param args Loader arguments
     * @return Loader object
     */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new GetRecipeLoader(getApplicationContext(), mRecipe.getDatabaseId());
    }

    /**
     * Loader finishes its execution
     * @param loader Loader object
     * @param data Loader data: recipe
     */
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == LOADER_ID) {
            mDatabaseRecipe = (Recipe) data;
            onRecipesLoaded();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}
}