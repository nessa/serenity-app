package com.amusebouche.activities;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Add activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Add app activity (recipe creation).
 *
 * Related layouts:
 * - Content: activity_edition.xml.xml
 */
public class EditionActivity extends AppCompatActivity {

    // Tabs
    private int FIRST_TAB = 0;
    private int SECOND_TAB = 1;
    private int THIRD_TAB = 2;

    // Data variables
    private Recipe mRecipe;
    private Recipe mControlRecipe;
    private ArrayList<String> mForcedCategories;

    // UI
    private View mLayout;
    private TabLayout mTabs;

    // FABs
    private FloatingActionButton mAddIngredientButton;
    private FloatingActionButton mAddDirectionButton;

    // Fragments
    private RecipeEditionFirstTabFragment mFirstFragment;
    private RecipeEditionSecondTabFragment mSecondFragment;
    private RecipeEditionThirdTabFragment mThirdFragment;
    private int mLastTab;

    // Behaviour variables
    private boolean mEnableSaveButton;

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

        // Get preferences
        String mRecipesLanguage = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);

        // Get data from previous activity
        int presentTab = FIRST_TAB;
        String title = getString(R.string.activity_edit_title);
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipe = i.getParcelableExtra(AppData.INTENT_KEY_RECIPE);

            if (mRecipe == null) {
                // Create an empty recipe
                mRecipe = new Recipe(
                    "",
                    "",
                    "",
                    "", // Local recipes has no username declared until they are uploaded
                    mRecipesLanguage, // Set preferences language
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
                    false,
                    new ArrayList<RecipeCategory>(),
                    new ArrayList<RecipeIngredient>(),
                    new ArrayList<RecipeDirection>());

                mControlRecipe = null;

                title = getString(R.string.activity_add_title);
            }
        } else {
            mRecipe = savedInstanceState.getParcelable(AppData.INTENT_KEY_RECIPE);
            mControlRecipe = mRecipe;
            presentTab = savedInstanceState.getInt(AppData.INTENT_KEY_EDITION_TAG);
        }

        mLastTab = presentTab;
        mEnableSaveButton = false;

        resetForcedCategories();

        // Set view
        overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
        setContentView(R.layout.activity_edition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mLayout = findViewById(R.id.coordinator_layout);

        // Set tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        // TODO: Check this!
        viewPager.setCurrentItem(presentTab);

        mTabs = (TabLayout) findViewById(R.id.tab_layout);
        mTabs.setupWithViewPager(viewPager);

        // Set tab layout styles

        int tabIconColor = ContextCompat.getColor(this, android.R.color.white);
        Drawable d;

        TabLayout.Tab firstTab = mTabs.getTabAt(FIRST_TAB);
        if (firstTab != null) {
            d = getDrawable(R.drawable.ic_description_32dp);
            if (d != null) {
                d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                firstTab.setIcon(d);
            }
        }

        TabLayout.Tab secondTab = mTabs.getTabAt(SECOND_TAB);
        if (secondTab != null) {
            d = getDrawable(R.drawable.ic_ingredients_32dp);
            if (d != null) {
                d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                secondTab.setIcon(d);
            }
        }

        TabLayout.Tab thirdTab = mTabs.getTabAt(THIRD_TAB);
        if (thirdTab != null) {
            d = getDrawable(R.drawable.ic_cook_32dp);
            if (d != null) {
                d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                thirdTab.setIcon(d);
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // Hide previous FABs
                if (mLastTab == SECOND_TAB) {
                    mAddIngredientButton.hide();
                }
                if (mLastTab == THIRD_TAB) {
                    mAddDirectionButton.hide();
                }

                // Select new tab
                TabLayout.Tab tab = mTabs.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }

                // Reload initial data
                if (position == FIRST_TAB) {
                    mFirstFragment.onReloadView();
                }

                // Show FABs again
                if (position == SECOND_TAB) {
                    mAddIngredientButton.show();
                }
                if (position == THIRD_TAB) {
                    mAddDirectionButton.show();
                }

                mLastTab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}

        });


        // Set FABs
        mAddIngredientButton = (FloatingActionButton) findViewById(R.id.add_ingredient_button);
        mAddDirectionButton = (FloatingActionButton) findViewById(R.id.add_direction_button);

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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(AppData.INTENT_KEY_RECIPE, mRecipe);
        outState.putInt(AppData.INTENT_KEY_EDITION_TAG, mTabs.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        onBack();
        super.onBackPressed();
    }

    // UI METHODS

    /**
     * Define fragments and add them to the view pager adapter
     * @param viewPager Loaded view pager
     */
    private void setupViewPager(ViewPager viewPager) {
        mFirstFragment = new RecipeEditionFirstTabFragment();
        mSecondFragment = new RecipeEditionSecondTabFragment();
        mThirdFragment = new RecipeEditionThirdTabFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mFirstFragment);
        adapter.addFragment(mSecondFragment);
        adapter.addFragment(mThirdFragment);
        viewPager.setAdapter(adapter);
    }


    // MENU METHODS

    /**
     *  Inflate the menu items to use them in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_edition, menu);
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
        this.setResult(RESULT_OK, intent);
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
     * Enable save button if the recipe has a title, some ingredients and some directions.
     */
    public void checkEnableSaveButton() {
        mEnableSaveButton = mRecipe.getTitle().length() > 0 && mRecipe.getIngredients().size() > 0 &&
            mRecipe.getDirections().size() > 0;
        if (mRecipe.getImage().length() > 0) {
            mEnableSaveButton = mEnableSaveButton && URLUtil.isValidUrl(mRecipe.getImage());
        }
    }

    // FUNCTIONALITY METHODS

    /**
     * Method called when the menu item "save" is pushed.
     * It saves the recipe on the database.
     */
    public void onSaveClicked() {
        if (mEnableSaveButton) {
            /* Update recipe if:
             * - It's from API and exists a recipe with its API id in the database
             * - It's not from API and it exists in database
             * Otherwise, create a new recipe
             */
            if (mControlRecipe != null) {
                mRecipe.setIsUpdated(mRecipe.diff(mControlRecipe));
            } else {
                mRecipe.setIsUpdated(true);
            }

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
            // Button disabled
            Snackbar.make(mLayout, getString(R.string.recipe_edition_fill_recipe_message),
                Snackbar.LENGTH_LONG)
                .show();
        }
    }

    // RECIPE METHODS

    private void resetForcedCategories() {
        if (mForcedCategories == null) {
            mForcedCategories = new ArrayList<>();
        } else {
            mForcedCategories.clear();
        }

        for (RecipeIngredient ingredient : mRecipe.getIngredients()) {

            if (mDatabaseHelper.existIngredient(ingredient.getName())) {

                Ingredient ing = mDatabaseHelper.getIngredientByTranslation(ingredient.getName());

                ArrayList<String> categories = new ArrayList<>(Arrays.asList(ing.getCategories().split(
                    Pattern.quote(Ingredient.CATEGORY_SEPARATOR))));

                for (String c : categories) {
                    boolean found = false;

                    for (String fc : mForcedCategories) {
                        if (fc.equals(c)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        mForcedCategories.add(c);
                    }
                }
            }
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}