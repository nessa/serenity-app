package com.amusebouche.activities;


import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.amusebouche.data.Ingredient;
import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.fragments.RecipeEditionFirstTabFragment;
import com.amusebouche.services.AppData;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.UserFriendlyTranslationsHandler;
import com.amusebouche.fragments.RecipeEditionSecondTabFragment;
import com.amusebouche.fragments.RecipeEditionThirdTabFragment;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;


/**
 * Add activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Add app activity (recipe creation).
 *
 * Related layouts:
 * - Content: activity_add.xml
 */
public class EditionActivity extends AppCompatActivity {

    // Tab identifiers
    private static final String TAB_1 = "first_tab";
    private static final String TAB_2 = "second_tab";
    private static final String TAB_3 = "third_tab";

    // Saved data keys
    private static final String INTENT_KEY_RECIPE = "recipe";
    private static final String PARCELABLE_RECIPES_KEY = "recipes";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String LIMIT_PER_PAGE_KEY = "limit";
    private static final String INTENT_KEY_TAB = "tab";

    // Data variables
    private Recipe mRecipe;
    private ArrayList<String> mForcedCategories;
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;

    // UI
    private View mLayout;

    // Tabs
    private TabHost mTabs;
    private String mLastTab;

    // FABs
    private ActionButton mAddIngredientButton;
    private ActionButton mAddDirectionButton;

    // Fragments
    private RecipeEditionFirstTabFragment mFirstFragment;
    private RecipeEditionSecondTabFragment mSecondFragment;
    private RecipeEditionThirdTabFragment mThirdFragment;

    // Behaviour variables
    private boolean mEnableSaveButton;
    private boolean mRecipeUpdated;

    // Services variables
    private DatabaseHelper mDatabaseHelper;

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

        // Get database helper
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        mRecipeUpdated = false;

        // Get data from previous activity
        String presentTab = TAB_1;
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipe = i.getParcelableExtra(INTENT_KEY_RECIPE);
            mRecipes = i.getParcelableArrayListExtra(PARCELABLE_RECIPES_KEY);
            mCurrentPage = i.getIntExtra(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = i.getIntExtra(LIMIT_PER_PAGE_KEY, 0);

            if (mRecipe == null) {
                // Create an empty recipe
                mRecipe = new Recipe(
                        "",
                        "",
                        "",
                        "", // TODO: Set username
                        "es", // TODO: Set preferences language
                        UserFriendlyTranslationsHandler.getDefaultTypeOfDish(),
                        UserFriendlyTranslationsHandler.getDefaultDifficulty(),
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
            }
        } else {
            mRecipe = savedInstanceState.getParcelable(INTENT_KEY_RECIPE);
            mRecipes = savedInstanceState.getParcelableArrayList(PARCELABLE_RECIPES_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY, 0);
            mLimitPerPage = savedInstanceState.getInt(LIMIT_PER_PAGE_KEY, 0);
            presentTab = savedInstanceState.getString(INTENT_KEY_TAB);
        }

        resetForcedCategories();

        overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setTitle(getString(R.string.activity_add_title));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayout = findViewById(R.id.layout);

        // Get fragments
        mFirstFragment = (RecipeEditionFirstTabFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        mSecondFragment = (RecipeEditionSecondTabFragment) getFragmentManager().findFragmentById(R.id.fragment2);
        mThirdFragment = (RecipeEditionThirdTabFragment) getFragmentManager().findFragmentById(R.id.fragment3);

        // Get FABS
        mAddIngredientButton = (ActionButton)findViewById(R.id.add_ingredient_button);
        mAddDirectionButton = (ActionButton)findViewById(R.id.add_direction_button);

        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSecondFragment.showAddDialog();
            }
        });

        mAddDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThirdFragment.showAddDialog();
            }
        });

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

        // Show first tab at the beginning
        mTabs.setCurrentTabByTag(presentTab);
        mLastTab = presentTab;
        resetTabColors();

        mTabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(final String tabId) {
                // Hide previous FABs
                if (mLastTab.equals(TAB_2)) {
                    mAddIngredientButton.hide();
                }
                if (mLastTab.equals(TAB_3)) {
                    mAddDirectionButton.hide();
                }

                mTabs.setCurrentTabByTag(tabId);
                resetTabColors();

                // Reload initial data
                if (tabId.equals(TAB_1)) {
                    mFirstFragment.reloadInitialData();
                }

                // Show FABs after a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tabId.equals(TAB_2)) {
                            mAddIngredientButton.show();
                        }
                        if (tabId.equals(TAB_3)) {
                            mAddDirectionButton.show();
                        }

                        mLastTab = tabId;
                    }
                }, 400);

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
        onBack();
        super.onBackPressed();
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

    // MENU METHODS

    /**
     *  Inflate the menu items to use them in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Implement actions to run when a menu item is selected.
     *
     * @param item Pushed menu item
     * @return A control boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                super.onBackPressed();
                return true;
            case R.id.action_save:
                onSaveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onBack() {
        Intent intent = new Intent();
        intent.putExtra(AppData.INTENT_KEY_RECIPE, mRecipe);
        setResult(RESULT_OK, intent);
    }


    // GETTERS

    /**
     * Get recipe method to share it with its fragments.
     * @return Recipe object
     */
    public Recipe getRecipe() {
        return mRecipe;
    }


    public ArrayList<String> getForcedCategories() {
        return mForcedCategories;
    }

    // SETTERS

    /**
     * Set boolean that enables save button
     * @param enable boolean
     */
    public void setEnableSaveButton(boolean enable) {
        mEnableSaveButton = enable;
    }

    // FUNCTIONALITY METHODS

    /**
     * Method called when the menu item "save" is pushed.
     * It saves the recipe on the database.
     *
     * TODO: Not implemented yet
     */
    public void onSaveClicked() {
        if (mEnableSaveButton) {
            /* Update recipe if:
             * - It's from API and exists a recipe with its API id in the database
             * - It's not from API and it exists in database
             * Otherwise, create a new recipe
             */
            if ((mRecipe.getIsOnline() && mDatabaseHelper.existRecipeWithAPIId(mRecipe.getId())) ||
                    (!mRecipe.getIsOnline() && mRecipe.getDatabaseId() != null &&
                            !mRecipe.getDatabaseId().equals(""))) {
                mDatabaseHelper.updateRecipe(mRecipe);
            } else {
                mDatabaseHelper.createRecipe(mRecipe);
            }

            // Post save
            Snackbar.make(mLayout, getString(R.string.recipe_edition_saved_recipe_message),
                    Snackbar.LENGTH_LONG)
                    .show();
        } else {
            Log.d("NOTE", "BUTTON DISABLED");
        }
    }

    // RECIPE METHODS

    private void resetForcedCategories() {
        if (mForcedCategories == null) {
            mForcedCategories = new ArrayList<>();
        } else {
            mForcedCategories.clear();
        }

        boolean uncategorized = false;

        for (RecipeIngredient ingredient : mRecipe.getIngredients()) {

            if (mDatabaseHelper.existIngredient(ingredient.getName())) {

                Ingredient ing = mDatabaseHelper.getIngredientByTranslation(ingredient.getName());

                ArrayList<String> categories = new ArrayList<>(Arrays.asList(ing.getCategories().split(
                    Pattern.quote(Ingredient.CATEGORY_SEPARATOR))));

                for (String c : categories) {
                    boolean found = false;

                    for (RecipeCategory rc : mRecipe.getCategories()) {
                        if (rc.getName().equals(c)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        mRecipe.getCategories().add(new RecipeCategory(c));
                    }
                }
            } else {
                uncategorized = true;
            }
        }

        if (uncategorized) {
            mRecipe.getCategories().add(new RecipeCategory(RecipeCategory.CATEGORY_UNCATEGORIZED));
        }

    }

    // FRAGMENT METHODS

    /**
     * Enable or disable the given button
     * @param button Given button
     * @param enabled Boolean: if true, enable the button
     *                If false, disable it
     */
    public void toggleEnableButton(Button button, boolean enabled) {
        button.setEnabled(enabled);

        if (enabled) {
            button.setTextColor(getResources().getColor(R.color.theme_default_accent));
        } else {
            button.setTextColor(getResources().getColor(R.color.secondary_text));
        }
    }

    /**
     * Checks text field validations
     * @param textView Text view element to check
     * @return True if the text view string is correct. Otherwise, false.
     */
    public boolean checkRequiredValidation(TextView textView) {
        if (textView.getText().toString().length() == 0) {
            textView.setError(getString(R.string.recipe_edition_error_required));
            return false;
        }

        return true;
    }

    /**
     * Checks URL validations
     * @param textView Text view element to check
     * @return True if the string is a valid URL. Otherwise, false.
     */
    public boolean checkURLValidation(TextView textView) {
        if (textView.getText().toString().length() > 0 && !URLUtil.isValidUrl(textView.getText().toString())) {
            textView.setError(getString(R.string.recipe_edition_error_invalid_uri));
            return false;
        }

        return true;
    }

}