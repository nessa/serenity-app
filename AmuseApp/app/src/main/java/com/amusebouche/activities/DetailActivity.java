package com.amusebouche.activities;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amusebouche.fragments.RecipeDetailFirstTabFragment;
import com.amusebouche.fragments.RecipeDetailFourthTabFragment;
import com.amusebouche.fragments.RecipeDetailSecondTabFragment;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.data.Recipe;
import com.amusebouche.fragments.RecipeDetailThirdTabFragment;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.AppData;
import com.amusebouche.services.ImageHandler;
import com.amusebouche.services.RequestHandler;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class DetailActivity extends AppCompatActivity {

    // Data variables
    private int mRecipePosition;
    private Recipe mRecipe;
    private Recipe mAPIRecipe;
    private Recipe mDatabaseRecipe;
    private boolean mRecognizerLanguageSetting;

    // UI
    private TextView mTitle;
    private Bitmap mMainImage;
    private TabLayout mTabs;
    private ImageView mRecipeImage;
    private View mLayout;
    private Snackbar mSnackbar;

    // Fragments
    private RecipeDetailFirstTabFragment mFirstFragment;
    private RecipeDetailSecondTabFragment mSecondFragment;
    private RecipeDetailThirdTabFragment mThirdFragment;

    // User data
    private String mUsername;
    private String mToken;
    private boolean isUserLoggedIn;

    // Behaviour variables
    private boolean mInitialDownload = true;
    private boolean mOfflineModeSetting;
    private boolean mWifiModeSetting;

    // Services
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

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        // Get preferences
        String recognizerLanguageString = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE);
        String offlineModeString = mDatabaseHelper.getAppData(AppData.PREFERENCE_OFFLINE_MODE);
        String wifiModeString = mDatabaseHelper.getAppData(AppData.PREFERENCE_WIFI_MODE);
        mRecognizerLanguageSetting = recognizerLanguageString.equals(AppData.PREFERENCE_TRUE_VALUE);
        mOfflineModeSetting = offlineModeString.equals(AppData.PREFERENCE_TRUE_VALUE);
        mWifiModeSetting = wifiModeString.equals(AppData.PREFERENCE_TRUE_VALUE);

        // Check if user is logged in
        mToken = mDatabaseHelper.getAppData(AppData.USER_AUTH_TOKEN);
        mUsername = mDatabaseHelper.getAppData(AppData.USER_LOGGED_USERNAME);
        isUserLoggedIn = !mToken.equals("");

        mInitialDownload = true;

        // Get saved data
        int presentTab = 0;
        if (savedInstanceState == null) {
            Intent i = getIntent();
            mRecipe = i.getParcelableExtra(AppData.INTENT_KEY_RECIPE);
            mRecipePosition = i.getIntExtra(AppData.INTENT_KEY_RECIPE_POSITION, -1);
        } else {
            mRecipe = savedInstanceState.getParcelable(AppData.INTENT_KEY_RECIPE);
            mRecipePosition = savedInstanceState.getInt(AppData.INTENT_KEY_RECIPE_POSITION, -1);
            presentTab = savedInstanceState.getInt(AppData.INTENT_KEY_DETAIL_TAG);
        }

        // Get image bitmap from file
        String FILENAME = AppData.RECIPE_TRANSITION_IMAGE;
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

        // Set view
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mLayout = findViewById(R.id.layout);


        // Set tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        // TODO: Check this!
        viewPager.setCurrentItem(presentTab);

        mTabs = (TabLayout) findViewById(R.id.tab_layout);
        //mTabs.setBackgroundColor(getResources().getColor(R.color.theme_default_primary));
        mTabs.setupWithViewPager(viewPager);

        // Set tab layout styles

        int tabIconColor = ContextCompat.getColor(this, android.R.color.white);
        Drawable d;

        // getResources().getDrawable() is deprecated on API 22 (LOLLIPOP_MR1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            TabLayout.Tab firstTab = mTabs.getTabAt(0);
            if (firstTab != null) {
                d = getDrawable(R.drawable.ic_description_32dp);
                if (d != null) {
                    d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    firstTab.setIcon(d);
                }
            }

            TabLayout.Tab secondTab = mTabs.getTabAt(1);
            if (secondTab != null) {
                d = getDrawable(R.drawable.ic_ingredients_32dp);
                if (d != null) {
                    d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    secondTab.setIcon(d);
                }
            }

            TabLayout.Tab thirdTab = mTabs.getTabAt(2);
            if (thirdTab != null) {
                d = getDrawable(R.drawable.ic_restaurant_menu_white_36dp);
                if (d != null) {
                    thirdTab.setIcon(d);
                }
            }

            TabLayout.Tab fourthTab = mTabs.getTabAt(3);
            if (fourthTab != null) {
                d = getDrawable(R.drawable.ic_comments_32dp);
                if (d != null) {
                    d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    fourthTab.setIcon(d);
                }
            }
        } else {
            TabLayout.Tab firstTab = mTabs.getTabAt(0);
            if (firstTab != null) {
                d = getResources().getDrawable(R.drawable.ic_description_32dp);
                if (d != null) {
                    d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    firstTab.setIcon(d);
                }
            }

            TabLayout.Tab secondTab = mTabs.getTabAt(1);
            if (secondTab != null) {
                d = getResources().getDrawable(R.drawable.ic_ingredients_32dp);
                if (d != null) {
                    d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    secondTab.setIcon(d);
                }
            }

            TabLayout.Tab thirdTab = mTabs.getTabAt(2);
            if (thirdTab != null) {
                d = getResources().getDrawable(R.drawable.ic_restaurant_menu_white_36dp);
                if (d != null) {
                    thirdTab.setIcon(d);
                }
            }

            TabLayout.Tab fourthTab = mTabs.getTabAt(3);
            if (fourthTab != null) {
                d = getResources().getDrawable(R.drawable.ic_comments_32dp);
                if (d != null) {
                    d.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    fourthTab.setIcon(d);
                }
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = mTabs.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
                resetBarTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

        // Set title
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        resetBarTitle(presentTab);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Define UI
        mRecipeImage = (ImageView) findViewById(R.id.recipe_image);

        onReloadView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get check recipes
        getCheckRecipes();
    }

    /**
     * Called to retrieve per-instance state from an activity before being killed so
     * the state can be restored in onCreate method.
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(AppData.INTENT_KEY_RECIPE, mRecipe);
        outState.putInt(AppData.INTENT_KEY_RECIPE_POSITION, mRecipePosition);
        outState.putInt(AppData.INTENT_KEY_DETAIL_TAG, mTabs.getSelectedTabPosition());

        super.onSaveInstanceState(outState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppData.REQUEST_FROM_EDITION_TO_DETAIL_CODE) {
            if (resultCode == RESULT_OK) {
                mRecipe = data.getParcelableExtra(AppData.INTENT_KEY_RECIPE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // UI METHODS

    /**
     * Define fragments and add them to the view pager adapter
     * @param viewPager Loaded view pager
     */
    private void setupViewPager(ViewPager viewPager) {
        mFirstFragment = new RecipeDetailFirstTabFragment();
        mSecondFragment = new RecipeDetailSecondTabFragment();
        mThirdFragment = new RecipeDetailThirdTabFragment();
        RecipeDetailFourthTabFragment mFourthFragment = new RecipeDetailFourthTabFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mFirstFragment);
        adapter.addFragment(mSecondFragment);
        adapter.addFragment(mThirdFragment);
        adapter.addFragment(mFourthFragment);
        viewPager.setAdapter(adapter);
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");

        boolean goBack = true;
        if (mTabs.getSelectedTabPosition() == 2) {
            if (mThirdFragment != null && mThirdFragment.isInOngoingMode()) {
                mThirdFragment.exitFromOngoingMode();
                goBack = false;
            }
        }

        if (goBack) {
            Intent intent = new Intent();
            intent.putExtra(AppData.INTENT_KEY_RECIPE, mRecipe);
            intent.putExtra(AppData.INTENT_KEY_RECIPE_POSITION, mRecipePosition);
            setResult(RESULT_OK, intent);
            DetailActivity.super.onBackPressed();
        }
    }

    /**
     * Reload all UI elements (and fragments) with the mRecipe data.
     */
    public void onReloadView() {
        // Reset UI elements
        mRecipeImage.setImageBitmap(mMainImage);
        /* TODO: Update image when recipe changes
        if (!mRecipe.getImage().equals("")) {
            ImageHandler.setCellImage(this, mRecipe.getImage(), mRecipeImage, mRecipePosition);
        } else {
            mRecipeImage.setImageBitmap(mMainImage);
        }*/
    }

    public void onReloadFragmentViews() {
        // Reset fragment components
        if (mFirstFragment != null) {
            mFirstFragment.onReloadView();
        }
        if (mSecondFragment != null) {
            mSecondFragment.onReloadView();
        }
        if (mThirdFragment != null) {
            mThirdFragment.onReloadView();
        }
    }

    /**
     * Change action bar title every time we select a new tab.
     * @param tab Selected tab
     */
    private void resetBarTitle(int tab) {
        switch (tab) {
            case 1:
                mTitle.setText(getString(R.string.detail_ingredients_label));
                break;
            case 2:
                mTitle.setText(getString(R.string.detail_directions_label));
                break;
            case 3:
                mTitle.setText(getString(R.string.detail_comments_label));
                break;
            default:
            case 0:
                mTitle.setText(getString(R.string.detail_information_label));
                break;
        }
    }

    // UI METHODS

    public void showBasicSnackbar(String message) {
        Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void showLoadingSnackbar(String message) {
        // Show loading view
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF,
            android.graphics.PorterDuff.Mode.MULTIPLY);

        mSnackbar = Snackbar.make(mLayout, message, Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snack_view = (Snackbar.SnackbarLayout) mSnackbar.getView();
        snack_view.addView(progressBar);
        mSnackbar.show();
    }

    public void hideLoadingSnackbar() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    // MENU METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
        downloadItem.setVisible(!mOfflineModeSetting && (mDatabaseRecipe == null || (mAPIRecipe != null &&
            mAPIRecipe.getUpdatedTimestamp().getTime() > mDatabaseRecipe.getUpdatedTimestamp().getTime())));

        /* Upload item is enabled when:
         * - Recipe doesn't exist on API
         */
        MenuItem uploadItem = menu.findItem(R.id.action_upload);
        uploadItem.setVisible(!mOfflineModeSetting && isUserLoggedIn && mDatabaseRecipe != null &&
                mDatabaseRecipe.getIsUpdated());

        /* Rate item is enabled when:
         * - User is logged in
         */
        MenuItem rateItem = menu.findItem(R.id.action_rate);
        rateItem.setVisible(!mOfflineModeSetting && isUserLoggedIn);

        /* Delete item is enabled when:
         * - Recipes is not online
         */
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        deleteItem.setVisible(!mRecipe.getIsOnline());

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
            case R.id.action_delete:
                deleteRecipe();
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

    /**
     * Get token method to share it with its fragments.
     * @return Authorization token
     */
    public String getToken() { return mToken; }

    public boolean getRecognizerLanguageSetting() {
        return mRecognizerLanguageSetting;
    }

    // FUNCTIONALITY METHODS

    /**
     * Show edition view
     */
    public void editRecipe() {
        Intent i = new Intent(this, EditionActivity.class);
        i.putExtra(AppData.INTENT_KEY_RECIPE, mRecipe);
        startActivityForResult(i, AppData.REQUEST_FROM_EDITION_TO_DETAIL_CODE);
    }

    /**
     * Download recipe and store it into the database
     */
    public void downloadRecipe() {
        if (!mWifiModeSetting || RequestHandler.isWifiConnected(DetailActivity.this)) {
            showLoadingSnackbar(getString(R.string.detail_saving_recipe_message));

            AmuseAPI mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            Call<ResponseBody> call = mAPI.getRecipe(mRecipe.getId());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    boolean fail = false;

                    if (response.code() == 200) {
                        String data = "";

                        // Get response data
                        if (response.body() != null) {
                            try {
                                data = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // Build objects from response data
                        if (!data.equals("")) {
                            try {
                                JSONObject jObject = new JSONObject(data);
                                mRecipe = new Recipe(jObject);
                                mRecipe.setIsOnline(true);

                                /* Update recipe if:
                                 * - It's from API and exists a recipe with its API id in the database
                                 * - It's not from API and it exists in database
                                 * Otherwise, create a new recipe
                                 */
                                if ((mRecipe.getIsOnline() &&
                                        mDatabaseHelper.existRecipeWithAPIId(mRecipe.getId())) ||
                                        (!mRecipe.getIsOnline() && mRecipe.getDatabaseId() != null &&
                                                !mRecipe.getDatabaseId().equals(""))) {
                                    mDatabaseHelper.updateRecipe(mRecipe);
                                } else {
                                    mDatabaseHelper.createRecipe(mRecipe);
                                }

                                // Post save
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.recipe_edition_saved_recipe_message),
                                        Snackbar.LENGTH_LONG)
                                        .show();
                                openOptionsMenu();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                fail = true;
                            }
                        } else {
                            fail = true;
                        }
                    } else {
                        fail = true;
                    }

                    if (fail) {
                        // Show error
                        hideLoadingSnackbar();
                        Snackbar.make(mLayout, getString(R.string.detail_saving_error_message),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Show error
                    hideLoadingSnackbar();
                    Snackbar.make(mLayout, getString(R.string.detail_saving_error_message),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        } else {
            Snackbar.make(mLayout, getString(R.string.detail_recipe_wifi_only_message),
                Snackbar.LENGTH_LONG)
                .show();
        }
    }

    /**
     * Upload recipe to the API
     */
    public void uploadRecipe() {
        if (isUserLoggedIn) {
            if (!mWifiModeSetting || RequestHandler.isWifiConnected(DetailActivity.this)) {
                showLoadingSnackbar(getString(R.string.detail_uploading_recipe_message));

                AmuseAPI api = RetrofitServiceGenerator.createService(AmuseAPI.class, mToken, true);

                if (mRecipe.getId().equals("") || mRecipe.getId().equals("0")) {
                    // Create
                    Call<ResponseBody> requestCall = api.createRecipe(mRecipe);

                    // Asynchronous call
                    requestCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 201) {
                                String data = "";

                                // Get response data
                                if (response.body() != null) {
                                    try {
                                        data = response.body().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Build objects from response data
                                if (!data.equals("")) {
                                    try {
                                        JSONObject jObject = new JSONObject(data);

                                        if (jObject.has("owner")) {
                                            mRecipe.setOwner(jObject.getString("owner"));
                                        }

                                        if (jObject.has("id")) {
                                            mRecipe.setId(jObject.getString("id"));
                                        }

                                        // Restore updated value
                                        mRecipe.setIsUpdated(false);

                                        if (!mRecipe.getDatabaseId().equals("")) {
                                            mDatabaseHelper.updateRecipe(mRecipe);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Post upload
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.detail_recipe_created_message),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            } else {
                                // Show error
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.detail_uploading_error_message),
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Show error
                            hideLoadingSnackbar();
                            Snackbar.make(mLayout, getString(R.string.detail_uploading_error_message),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }

                    });
                } else {
                    // Update
                    Call<ResponseBody> requestCall = api.updateRecipe(mRecipe.getId(), mRecipe);

                    // Asynchronous call
                    requestCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.detail_recipe_updated_message),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            } else {
                                // Show error
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.detail_uploading_error_message),
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Show error
                            hideLoadingSnackbar();
                            Snackbar.make(mLayout, getString(R.string.detail_uploading_error_message),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Snackbar.make(mLayout, getString(R.string.detail_recipe_wifi_only_message),
                    Snackbar.LENGTH_LONG)
                    .show();
            }
        }
    }

    /**
     * Show rate recipe dialog.
     */
    public void rateRecipe() {
        final Dialog rateDialog = new Dialog(this);
        rateDialog.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setContentView(R.layout.dialog_rate_recipe);

        final RatingBar ratingBar = (RatingBar) rateDialog.findViewById(R.id.rating_bar);

        Button cancelButton = (Button) rateDialog.findViewById(R.id.cancel);
        final Button acceptButton = (Button) rateDialog.findViewById(R.id.accept);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateDialog.dismiss();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mWifiModeSetting || RequestHandler.isWifiConnected(DetailActivity.this)) {
                    showLoadingSnackbar(getString(R.string.detail_rating_recipe_message));

                    AmuseAPI mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class, mToken);
                    Call<ResponseBody> call = mAPI.rateRecipe(mRecipe.getId(), (int) ratingBar.getRating());

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 201) {
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.detail_recipe_rated_message),
                                    Snackbar.LENGTH_LONG)
                                    .show();

                                // Update view
                                reloadAPIRecipe();
                            } else {
                                // Show error
                                hideLoadingSnackbar();
                                Snackbar.make(mLayout, getString(R.string.detail_uploading_error_message),
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Show error
                            hideLoadingSnackbar();
                            Snackbar.make(mLayout, getString(R.string.detail_uploading_error_message),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });

                    // We close the dialog only if the creation/update result is OK
                    rateDialog.dismiss();
                } else {
                    Snackbar.make(mLayout, getString(R.string.detail_recipe_wifi_only_message),
                        Snackbar.LENGTH_LONG)
                        .show();
                }
            }
        });

        rateDialog.show();
    }


    /**
     * Delete recipe from the database
     */
    public void deleteRecipe() {
        final Dialog deleteDialog = new Dialog(this);
        deleteDialog.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.dialog_delete_recipe);

        Button cancelButton = (Button) deleteDialog.findViewById(R.id.cancel);
        final Button acceptButton = (Button) deleteDialog.findViewById(R.id.accept);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteRecipe(mRecipe);

                deleteDialog.dismiss();

                Intent intent = new Intent();
                intent.putExtra(AppData.INTENT_KEY_RECIPE_POSITION, mRecipePosition);
                intent.putExtra(AppData.INTENT_KEY_RECIPE_DELETED, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        deleteDialog.show();
    }

    /**
     * Loads recipes to check data: API and database recipes
     */
    private void getCheckRecipes() {
        if (mRecipe.getIsOnline()) {
            mAPIRecipe = mRecipe;

            if (mDatabaseHelper.existRecipeWithAPIId(mRecipe.getId())) {
                mDatabaseRecipe = mDatabaseHelper.getRecipeByAPIId(mRecipe.getId());
            }

            // TODO: Check this!
            if (mInitialDownload) {
                mInitialDownload = false;
                onRecipesLoaded();
            } else {
                onReloadView();
                onReloadFragmentViews();
            }
        } else {
            mDatabaseRecipe = mRecipe;

            if (!mOfflineModeSetting && !mRecipe.getId().equals("") && !mRecipe.getId().equals("0")) {

                if (!mWifiModeSetting || RequestHandler.isWifiConnected(this)) {
                    AmuseAPI api = RetrofitServiceGenerator.createService(AmuseAPI.class);

                    Call<ResponseBody> requestCall = api.getRecipe(mRecipe.getId());

                    // Asynchronous call
                    requestCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String data = "";

                            // Get response data
                            if (response.body() != null) {
                                try {
                                    data = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Build objects from response data
                            if (!Objects.equals(data, "")) {

                                try {
                                    JSONObject jObject = new JSONObject(data);
                                    mAPIRecipe = new Recipe(jObject);

                                    onRecipesLoaded();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                onRecipesLoaded();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            onRecipesLoaded();
                        }

                    });
                }
            }
        }
    }

    private void reloadAPIRecipe() {
        if (!mWifiModeSetting || RequestHandler.isWifiConnected(this)) {
            AmuseAPI api = RetrofitServiceGenerator.createService(AmuseAPI.class);

            Call<ResponseBody> requestCall = api.getRecipe(mRecipe.getId());

            // Asynchronous call
            requestCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String data = "";

                    // Get response data
                    if (response.body() != null) {
                        try {
                            data = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Build objects from response data
                    if (!Objects.equals(data, "")) {

                        try {
                            JSONObject jObject = new JSONObject(data);
                            mRecipe = new Recipe(jObject);

                            onReloadView();
                            onReloadFragmentViews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}

            });
        }
    }

    /**
     * Reloads bar menu when all recipes are loaded
     */
    private void onRecipesLoaded() {
        openOptionsMenu();
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