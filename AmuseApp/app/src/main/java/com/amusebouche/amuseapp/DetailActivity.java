package com.amusebouche.amuseapp;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.amusebouche.data.Recipe;

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

    private static final String TAB_1 = "first_tab";
    private static final String TAB_2 = "second_tab";
    private static final String TAB_3 = "third_tab";
    private static final String TAB_4 = "fourth_tab";

    // Data variables
    private Recipe mRecipe;
    private Bitmap mMainImage;
    private ImageView mRecipeImage;
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

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable("recipe");
        } else {
            Intent i = getIntent();
            mRecipe = i.getParcelableExtra("recipe");
        }


        // Get image bitmap from file
        mMainImage = null;
        String FILENAME = "presentRecipeImage.png";
        try {
            FileInputStream is = this.openFileInput(FILENAME);
            mMainImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_detail);


        mRecipeImage = (ImageView) findViewById(R.id.recipe_image);
        mRecipeImage.setImageBitmap(mMainImage);


        mTabs = (TabHost)findViewById(android.R.id.tabhost);
        mTabs.setup();


        TabHost.TabSpec spec = mTabs.newTabSpec(TAB_1);
        spec.setContent(R.id.tab1);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_description_32dp));

        mTabs.addTab(spec);

        spec = mTabs.newTabSpec(TAB_2);
        spec.setContent(R.id.tab2);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_ingredients_32dp));
        mTabs.addTab(spec);

        spec = mTabs.newTabSpec(TAB_3);
        spec.setContent(R.id.tab3);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_cook_32dp));
        mTabs.addTab(spec);

        spec = mTabs.newTabSpec(TAB_4);
        spec.setContent(R.id.tab4);
        spec.setIndicator("", getResources().getDrawable(R.drawable.ic_comments_32dp));
        mTabs.addTab(spec);

        // Show first tab
        mTabs.setCurrentTabByTag(TAB_1);
        resetTabColors();

        mTabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mTabs.setCurrentTabByTag(tabId);
                resetTabColors();
            }
        });

        // Set bar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mRecipe.getTitle());
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
        outState.putParcelable("recipe", mRecipe);
    }


    // UI METHODS

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");

        // TODO: Check this!
        boolean goBack = true;
        if (mTabs.getCurrentTabTag().equals(TAB_3)) {
            RecipeDetailThirdTabFragment frag = (RecipeDetailThirdTabFragment) getFragmentManager()
                    .findFragmentById(R.id.container);

            if (frag.isInOngoingMode()) {
                frag.exitFromOngoingMode();
                goBack = false;
            }
        }

        if (goBack) {
            super.onBackPressed();
        }
    }


    // MENU METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_fav:
                makeFavorite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // GETTERS

    public Recipe getRecipe() { return mRecipe; }


    // FUNCTIONALITY METHODS

    public void makeFavorite() {
        Log.v("INFO", "Favorite X");
        // TODO: Implement this method.
    }

}