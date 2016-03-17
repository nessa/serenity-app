package com.amusebouche.amuseapp;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.UserFriendlyRecipeData;

import java.util.ArrayList;

/**
 * Recipe edtion fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of add activity and edit activity.
 * It contains lots of inputs to edit recipe's data.
 *
 * Related layouts:
 * - Content: fragment_recipe_edition.xml
 */
public class RecipeEditionFragment extends Fragment {


    private Recipe mRecipe;
    private LinearLayout mLayout;

    // LIFECYCLE METHODS

    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragemnt activity (DetailActivity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    /**
     * Called when the fragment's activity has been created and this fragment's view
     * hierarchy instantiated.
     *
     * @param savedInstanceState State of the fragment if it's being re-created.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    /**
     * Called to do initial creation of a fragment. This is called after onAttach and before
     * onCreateView.
     *
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

    }


    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
    }


    /**
     * Called when the Fragment is no longer started.
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop()");
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be
     * reconstructed in a new instance of its process is restarted.
     *
     * @param outState Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the fragment is no longer attached to its activity. Called after onDestroy.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * onCreate and onActivityCreated, onViewStateRestored, onStart().
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment,
     * @param container          This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If this fragment is being re-constructed from a previous saved
     *                           state as given here.
     * @return Return the View for the this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");


        mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_recipe_edition,
                container, false);

        if (getActivity() instanceof AddActivity) {
            // do something
            AddActivity x = (AddActivity) getActivity();

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
                    null,
                    "",
                    0,
                    0,
                    0,
                    "",
                    new ArrayList<RecipeCategory>(),
                    new ArrayList<RecipeIngredient>(),
                    new ArrayList<RecipeDirection>());
        } else {
            //do something else
            Log.d("INFO", "ELSE");
        }

        return mLayout;
    }

}
