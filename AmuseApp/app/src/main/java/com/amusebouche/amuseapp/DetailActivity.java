package com.amusebouche.amuseapp;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amusebouche.data.Recipe;


/**
 * Detail activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Detail app activity (recipe detail).
 * It has:
 * - The present fragment that is active.
 *
 * Related layouts:
 * - Menu: menu_recipe_detail.xml
 * - Content: activity_detail.xml
 */
public class DetailActivity extends ActionBarActivity {
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        mRecipe = i.getParcelableExtra("recipe");

        setContentView(R.layout.activity_detail);

        this.setBarTitle(mRecipe.getTitle());
    }


    public Recipe getRecipe() { return mRecipe; }

    public void setBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    /*
    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("DETAIL", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("DETAIL", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
    */

    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");


        RecipeDetailFragment frag = (RecipeDetailFragment) getFragmentManager()
                .findFragmentById(R.id.container);

        // Scroll up before get back (to make transition work perfectly)
        if (frag.getScrollView().getCurrentScrollY() > 0) {
            frag.scrollUp();
        } else {
            super.onBackPressed();
        }
    }

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


    public void makeFavorite() {
        Log.v("INFO", "Favorite X");
        // TODO: Implement this method.
    }

}