package com.amusebouche.amuseapp;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.UserFriendlyRecipeData;

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

    private static final String TAB_1 = "first_tab";
    private static final String TAB_2 = "second_tab";
    private static final String TAB_3 = "third_tab";

    private static final String INTENT_KEY_RECIPE = "recipe";
    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";
    private static final String INTENT_KEY_TAB = "tab";


    // Data variables
    private Recipe mRecipe;
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;

    private TabHost mTabs;

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
        String presentTab = TAB_1;
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipes = i.getParcelableArrayListExtra(PARCELABLE_RECIPES_KEY);
            mCurrentPage = i.getIntExtra(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = i.getIntExtra(LIMIT_PER_PAGE_KEY, 0);

            // Create an empty recipe
            mRecipe = new Recipe(
                    "",
                    "",
                    "",
                    "", // Set username
                    "es", // Set preferences language
                    UserFriendlyRecipeData.getDefaultTypeOfDish(),
                    UserFriendlyRecipeData.getDefaultDifficulty(),
                    null,
                    null,
                    0.0F,
                    "",
                    0,
                    0,
                    1,
                    "",
                    new ArrayList<RecipeCategory>(),
                    new ArrayList<RecipeIngredient>(),
                    new ArrayList<RecipeDirection>());
        } else {
            mRecipe = savedInstanceState.getParcelable(INTENT_KEY_RECIPE);
            mRecipes = savedInstanceState.getParcelableArrayList(PARCELABLE_RECIPES_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = savedInstanceState.getInt(LIMIT_PER_PAGE_KEY, 0);
            presentTab = savedInstanceState.getString(INTENT_KEY_TAB);
        }

        overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setTitle(getString(R.string.activity_add_title));


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

        // Show first tab at the beginning
        mTabs.setCurrentTabByTag(presentTab);
        resetTabColors();

        mTabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mTabs.setCurrentTabByTag(tabId);
                resetTabColors();
            }
        });
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

        outState.putParcelable(INTENT_KEY_RECIPE, mRecipe);
        outState.putString(INTENT_KEY_TAB, mTabs.getCurrentTabTag());
    }


    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    /**
     *  Inflate the menu items to use them in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_add, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveData();
                super.onBackPressed();
                return true;
            case R.id.action_save:
                onSaveClicked();
                return true;
            case R.id.action_add:
                onAddClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


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
     * Send main activity its proper data again
     */
    private void saveData() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PARCELABLE_RECIPES_KEY, mRecipes);
        intent.putExtra(CURRENT_PAGE_KEY, mCurrentPage);
        intent.putExtra(LIMIT_PER_PAGE_KEY, mLimitPerPage);

        setResult(RESULT_OK, intent);
    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    public void onSaveClicked() {
        Log.d("INFO", "SAVE CLICKED");
        Log.d("RECIPE TITLE", mRecipe.getTitle());
        Log.d("RECIPE IMAGEN", mRecipe.getImage());
        Log.d("RECIPE TIPO", mRecipe.getTypeOfDish());
        Log.d("RECIPE DIF", mRecipe.getDifficulty());
        Log.d("RECIPE TIME", mRecipe.getCookingTime().toString());
        Log.d("RECIPE SERV", mRecipe.getServings().toString());
        Log.d("RECIPE SOURCE", mRecipe.getSource());

        Log.d("RECIPE", "ING");
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            Log.d(String.format("%d",
                            ((RecipeIngredient) (mRecipe.getIngredients().get(i))).getSortNumber()),
                    ((RecipeIngredient) (mRecipe.getIngredients().get(i))).getName());
        }
    }

    public void onAddClicked() {
        if (!mTabs.getCurrentTabTag().equals(TAB_2)) {
            mTabs.setCurrentTabByTag(TAB_2);
        }

        RecipeEditionSecondTabFragment secondFragment = (RecipeEditionSecondTabFragment) getFragmentManager().findFragmentById(R.id.fragment2);
        secondFragment.showAddDialog();
    }

}