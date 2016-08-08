package com.amusebouche.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.adapters.AutoCompleteArrayAdapter;
import com.amusebouche.adapters.LeftMenuAdapter;
import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.fragments.InformationFragment;
import com.amusebouche.fragments.RecipeListFragment;
import com.amusebouche.fragments.SettingsFragment;
import com.amusebouche.fragments.UserFragment;
import com.amusebouche.services.AppData;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.RequestHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Main activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com>
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
public class MainActivity extends AppCompatActivity {

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

    // Services
    private DatabaseHelper mDatabaseHelper;

    // Data variables
    private ArrayList<Recipe> mRecipes;
    private Integer mCurrentPage;
    private Integer mLimitPerPage;
    private Integer mPreviousTotal;
    private ArrayList<Pair<String, ArrayList<String>>> mFilterParams;
    private String mRecipesLanguage;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        // Set initial data
        mCurrentPage = 0;
        mLimitPerPage = 10;
        mPreviousTotal = 0;
        mCurrentSelectedPosition = 1;

        // Get database helper
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        reloadRecipesLanguage();

        if (mDrawerLayout == null || mLeftDrawerView == null || mRightDrawerView == null || mDrawerToggle == null) {
            // Set left drawer
            loadLeftDrawer();
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
            case R.id.action_reload:
                selectItemInLeftMenu(mCurrentSelectedPosition);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // FILTER PARAMETERS

    /**
     * Right drawer's view constructor
     */
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
        final RelativeLayout dislikeIngredientsCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.dislike_ingredients_collapse);
        final RelativeLayout dislikeCategoriesCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.dislike_categories_collapse);
        final RelativeLayout categoriesCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.categories_collapse);
        final RelativeLayout difficultiesCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.difficulties_collapse);
        final RelativeLayout typesOfDishCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.types_of_dish_collapse);
        final RelativeLayout orderingCollapse = (RelativeLayout) mRightDrawerView.findViewById(R.id.ordering_collapse);

        // Get collapsible views
        final LinearLayout ingredientsCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.ingredients_collapsible_view);
        final LinearLayout dislikeIngredientsCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.dislike_ingredients_collapsible_view);
        final LinearLayout dislikeCategoriesCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.dislike_categories_collapsible_view);
        final LinearLayout categoriesCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.categories_collapsible_view);
        final LinearLayout difficultiesCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.difficulties_collapsible_view);
        final LinearLayout typesOfDishCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.types_of_dish_collapsible_view);
        final LinearLayout orderingCollapsibleView = (LinearLayout) mRightDrawerView.findViewById(R.id.ordering_collapsible_view);

        // Hide collapsible views
        ingredientsCollapsibleView.setVisibility(View.GONE);
        dislikeIngredientsCollapsibleView.setVisibility(View.GONE);
        dislikeCategoriesCollapsibleView.setVisibility(View.GONE);
        categoriesCollapsibleView.setVisibility(View.GONE);
        difficultiesCollapsibleView.setVisibility(View.GONE);
        typesOfDishCollapsibleView.setVisibility(View.GONE);
        orderingCollapsibleView.setVisibility(View.GONE);

        // Set listeners on clickable views
        ingredientsCollapse.setOnClickListener(mCollapseClickListener);
        dislikeIngredientsCollapse.setOnClickListener(mCollapseClickListener);
        dislikeCategoriesCollapse.setOnClickListener(mCollapseClickListener);
        categoriesCollapse.setOnClickListener(mCollapseClickListener);
        difficultiesCollapse.setOnClickListener(mCollapseClickListener);
        typesOfDishCollapse.setOnClickListener(mCollapseClickListener);
        orderingCollapse.setOnClickListener(mCollapseClickListener);


        // Set multi autocomplete views adapters
        final MultiAutoCompleteTextView ingredientsTextView = (MultiAutoCompleteTextView)
                mRightDrawerView.findViewById(R.id.ingredients_text_view);
        ingredientsTextView.setAdapter(new AutoCompleteArrayAdapter(this, mRecipesLanguage));
        ingredientsTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        final MultiAutoCompleteTextView dislikeIngredientsTextView = (MultiAutoCompleteTextView)
                mRightDrawerView.findViewById(R.id.dislike_ingredients_text_view);
        dislikeIngredientsTextView.setAdapter(new AutoCompleteArrayAdapter(this, mRecipesLanguage));
        dislikeIngredientsTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // Set multi autocomplete views buttons
        Button clearIngredientsFilterButton = (Button) mRightDrawerView.findViewById(R.id.clear_ingredients_filter);
        Button clearDislikeIngredientsFilterButton = (Button) mRightDrawerView.findViewById(R.id.clear_dislike_ingredients_filter);

        clearIngredientsFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsTextView.setText("");
            }
        });

        clearDislikeIngredientsFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dislikeIngredientsTextView.setText("");
            }
        });

        // Get dislike categories checkboxes
        final CheckBox glutenAllergyCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.allergy_gluten_checkbox);
        final CheckBox lactoseAllergyCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.allergy_lactose_checkbox);
        final CheckBox shellfishAllergyCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.allergy_shellfish_checkbox);
        final CheckBox fishAllergyCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.allergy_fish_checkbox);
        final CheckBox driedFruitsAllergyCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.allergy_dried_fruits_checkbox);

        // Get categories checkboxes
        final CheckBox mediterraneanDietCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.diet_mediterranean_checkbox);
        final CheckBox vegetarianDietheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.diet_vegetarian_checkbox);
        final CheckBox veganDietCheckbox = (CheckBox) mRightDrawerView.findViewById(R.id.diet_vegan_checkbox);

        // Get difficulties radio group
        final RadioGroup difficultiesRadioGroup = (RadioGroup) mRightDrawerView.findViewById(R.id.difficulties_radio_group);
        final RadioGroup typesOfDishRadioGroup = (RadioGroup) mRightDrawerView.findViewById(R.id.types_of_dish_radio_group);
        final RadioGroup orderingRadioGroup = (RadioGroup) mRightDrawerView.findViewById(R.id.ordering_radio_group);

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
                // Set title filter
                if (titleFilterTextView.getText().toString().length() > 0) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_TITLE,
                            titleFilterTextView.getText().toString(), true);
                } else {
                    removeFilterFromFilterParams(RequestHandler.API_PARAM_TITLE);
                }

                // Set ingredients filter
                removeFilterFromFilterParams(RequestHandler.API_PARAM_INGREDIENT);
                if (ingredientsTextView.getText().toString().length() > 0) {
                    ArrayList<String> strings = new ArrayList<>(Arrays.asList(
                            ingredientsTextView.getText().toString().split(",")));

                    for (String s : strings) {
                        addFilterToFilterParams(RequestHandler.API_PARAM_INGREDIENT, s.trim(), false);
                    }
                }

                // Set dislike ingredients filter
                removeFilterFromFilterParams(RequestHandler.API_PARAM_DISLIKE_INGREDIENT);
                if (dislikeIngredientsTextView.getText().toString().length() > 0) {
                    ArrayList<String> strings = new ArrayList<>(Arrays.asList(
                            dislikeIngredientsTextView.getText().toString().split(",")));

                    for (String s : strings) {
                        addFilterToFilterParams(RequestHandler.API_PARAM_DISLIKE_INGREDIENT,
                                s.trim(), false);
                    }
                }

                // Set dislike categories filter
                removeFilterFromFilterParams(RequestHandler.API_PARAM_DISLIKE_CATEGORY);

                // Gluten
                if (glutenAllergyCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_DISLIKE_CATEGORY,
                            RecipeCategory.CATEGORY_ALLERGY_GLUTEN_CODE, false);
                }

                // Lactose
                if (lactoseAllergyCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_DISLIKE_CATEGORY,
                        RecipeCategory.CATEGORY_ALLERGY_LACTOSE_CODE, false);
                }

                // Shellfish
                if (shellfishAllergyCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_DISLIKE_CATEGORY,
                        RecipeCategory.CATEGORY_ALLERGY_SHELLFISH_CODE, false);
                }

                // Fish
                if (fishAllergyCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_DISLIKE_CATEGORY,
                        RecipeCategory.CATEGORY_ALLERGY_FISH_CODE, false);
                }
                // Gluten
                if (driedFruitsAllergyCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_DISLIKE_CATEGORY,
                        RecipeCategory.CATEGORY_ALLERGY_DRIED_FRUIT_CODE, false);
                }


                // Set  categories filter
                removeFilterFromFilterParams(RequestHandler.API_PARAM_CATEGORY);

                // Mediterranean
                if (mediterraneanDietCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_CATEGORY,
                        RecipeCategory.CATEGORY_DIET_MEDITERRANEAN, false);
                }

                // Vegetarian
                if (vegetarianDietheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_CATEGORY,
                        RecipeCategory.CATEGORY_DIET_VEGETARIAN, false);
                }

                // Vegan
                if (veganDietCheckbox.isChecked()) {
                    addFilterToFilterParams(RequestHandler.API_PARAM_CATEGORY,
                        RecipeCategory.CATEGORY_DIET_VEGAN, false);
                }


                // Set difficulty filter
                switch (difficultiesRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.difficulty_low:
                        addFilterToFilterParams(RequestHandler.API_PARAM_DIFFICULTY,
                                Recipe.DIFFICULTY_LOW, true);
                        break;
                    case R.id.difficulty_medium:
                        addFilterToFilterParams(RequestHandler.API_PARAM_DIFFICULTY,
                            Recipe.DIFFICULTY_MEDIUM, true);
                        break;
                    case R.id.difficulty_high:
                        addFilterToFilterParams(RequestHandler.API_PARAM_DIFFICULTY,
                            Recipe.DIFFICULTY_HIGH, true);
                        break;
                    default:
                        removeFilterFromFilterParams(RequestHandler.API_PARAM_DIFFICULTY);
                }

                // Set type of dish filter
                switch (typesOfDishRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.type_of_dish_appetizer:
                        addFilterToFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH,
                            Recipe.TYPE_OF_DISH_APPETIZER, true);
                        break;
                    case R.id.type_of_dish_first_course:
                        addFilterToFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH,
                            Recipe.TYPE_OF_DISH_FIRST_COURSE, true);
                        break;
                    case R.id.type_of_dish_second_course:
                        addFilterToFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH,
                            Recipe.TYPE_OF_DISH_SECOND_COURSE, true);
                        break;
                    case R.id.type_of_dish_main_dish:
                        addFilterToFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH,
                            Recipe.TYPE_OF_DISH_MAIN_DISH, true);
                        break;
                    case R.id.type_of_dish_dessert:
                        addFilterToFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH,
                            Recipe.TYPE_OF_DISH_DESSERT, true);
                        break;
                    case R.id.type_of_dish_other:
                        addFilterToFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH,
                            Recipe.TYPE_OF_DISH_OTHER, true);
                        break;
                    default:
                        removeFilterFromFilterParams(RequestHandler.API_PARAM_TYPE_OF_DISH);
                }

                // Set ordering filter
                switch (orderingRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.ordering_last_updated:
                        addFilterToFilterParams(RequestHandler.API_PARAM_ORDERING,
                                "-" + RequestHandler.API_PARAM_UPDATED_TIMESTAMP, true);
                        break;
                    case R.id.ordering_best_rated:
                        addFilterToFilterParams(RequestHandler.API_PARAM_ORDERING,
                                "-" + RequestHandler.API_PARAM_RATING, true);
                        break;
                    case R.id.ordering_alphabetical:
                        addFilterToFilterParams(RequestHandler.API_PARAM_ORDERING,
                                RequestHandler.API_PARAM_TITLE, true);
                        break;
                    default:
                        removeFilterFromFilterParams(RequestHandler.API_PARAM_ORDERING);
                }

                mDrawerLayout.closeDrawer(mRightDrawerView);

                // Reset list fragment
                selectItemInLeftMenu(mCurrentSelectedPosition);
            }
        });

    }

    /**
     * Reset filter params and reset language parameter
     */
    private void resetFilterParams() {
        mFilterParams = new ArrayList<>();

        addFilterToFilterParams(RequestHandler.API_PARAM_PAGE_SIZE,
            this.getLimitPerPage().toString(), true);
        addFilterToFilterParams(RequestHandler.API_PARAM_ORDERING,
            "-" + RequestHandler.API_PARAM_UPDATED_TIMESTAMP, true);

        if (!mRecipesLanguage.equals("")) {
            addFilterToFilterParams(RequestHandler.API_PARAM_LANGUAGE, mRecipesLanguage, false);
        }
    }

    /**
     * Add a new value to a given parameter
     *
     * @param key Parameter key
     * @param value New parameter value
     * @param onlyOne If true, the key only MUST have one parameter.
     *                Otherwise, we can set multiple values
     */
    private void addFilterToFilterParams(String key, String value, boolean onlyOne) {
        if (value != null && !value.equals("")) {
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
    }

    /**
     * Remove a given key from the filter parameters
     * @param key Key to remove
     */
    private void removeFilterFromFilterParams(String key) {
        int found = -1;

        if (mFilterParams != null) {
            for (int i = 0; i < mFilterParams.size(); i++) {

                if (key.equals(mFilterParams.get(i).first)) {
                    found = i;
                }
            }
        }

        if (found > -1) {
            mFilterParams.remove(found);
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

    private void loadLeftDrawer() {
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
    }

    public void reloadLeftDrawer() {
        loadLeftDrawer();
        mLeftDrawerList.setItemChecked(mCurrentSelectedPosition, true);
    }

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
        boolean isRecipeList = true;
        switch (position) {
            case PROFILE:
                isRecipeList = false;
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
                isRecipeList = false;
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_settings));
                fragment = new SettingsFragment();
                break;
            case INFO:
                isRecipeList = false;
                getSupportActionBar().setTitle(getString(R.string.lateral_menu_info));
                fragment = new InformationFragment();
                break;
        }
/*
        // Hide add button
        if (!isRecipeList && mAddButton.isShown()) {
            mAddButton.hide();
        }*/

        // Enable or disable right drawer
        if (mDrawerLayout != null & mRightDrawerView != null) {
            if (isRecipeList) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mRightDrawerView);
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mRightDrawerView);
            }
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
/*
    public void showAddButton() {
        Log.d("MAIN", "SHOW ADD");
        mAddButton.clearAnimation();
        mAddButton.show();
    }*/

    private void resetRecipesAndSetMode(int mode) {
        if (mRecipes == null) {
            mRecipes = new ArrayList<>();
        } else {
            mRecipes.clear();
        }
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

    public String getRecipesLanguage() {
        return mRecipesLanguage;
    }

    /**
     * Returns the filter parameters.
     * Needed to transfer this information to the list fragment.
     * @return Filter parameters array
     */
    public ArrayList<Pair<String, ArrayList<String>>> getFilterParams() {
        return mFilterParams;
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


    // OTHERS

    /**
     * Reload recipes language preference
     */
    public void reloadRecipesLanguage() {
        mRecipesLanguage = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);
    }

}