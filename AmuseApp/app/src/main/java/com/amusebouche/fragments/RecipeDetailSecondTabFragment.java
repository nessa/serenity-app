package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.services.UserFriendlyTranslationsHandler;


/**
 * Recipe detail second tab fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of detail activity.
 * It's used inside a list of tabs.
 * It contains and shows the recipe ingredients.
 *
 * Related layouts:
 * - Content: fragment_detail_second_tab.xml
 */
public class RecipeDetailSecondTabFragment extends Fragment {

    // Father activity
    private DetailActivity mDetailActivity;

    // UI
    private LayoutInflater mInflater;
    private FrameLayout mLayout;
    private LinearLayout mIngredientsLayout;


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

        mDetailActivity = (DetailActivity) getActivity();

        mInflater = inflater;

        mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_detail_second_tab,
            container, false);

        mIngredientsLayout = (LinearLayout) mLayout.findViewById(R.id.ingredients);

        onReloadView();

        return mLayout;
    }
    
    /**
     * Reload all UI elements with the mDetailActivity.getRecipe() data.
     */
    public void onReloadView() {
        mIngredientsLayout.removeAllViews();
        
        for (int i = 0; i < mDetailActivity.getRecipe().getIngredients().size(); i++) {
            RecipeIngredient presentIngredient = mDetailActivity.getRecipe().getIngredients().get(i);

            LinearLayout ingredientLayout = (LinearLayout) mInflater.inflate(
                R.layout.item_detail_ingredient, mLayout, false);

            TextView quantity = (TextView) ingredientLayout.findViewById(R.id.quantity);
            quantity.setText(UserFriendlyTranslationsHandler.getIngredientQuantity(
                presentIngredient.getQuantity(), presentIngredient.getMeasurementUnit(),
                getActivity()));

            TextView name = (TextView) ingredientLayout.findViewById(R.id.name);
            name.setText(presentIngredient.getName());

            mIngredientsLayout.addView(ingredientLayout);
        }
    }

}
