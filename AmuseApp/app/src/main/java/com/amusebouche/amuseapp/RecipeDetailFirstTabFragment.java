package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.UserFriendlyRecipeData;

import java.util.Objects;

/**
 * Recipe detail first tab fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of detail activity.
 * It's used inside a list of tabs.
 * It contains and shows the recipe basic information.
 *
 * Related layouts:
 * - Content: fragment_detail_first_tab.xml
 */
public class RecipeDetailFirstTabFragment extends Fragment {

    // Data variables
    private Recipe mRecipe;

    // UI variables
    private FrameLayout mLayout;


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

        /* TODO: Try to prevent Skipped XX frames! The application may be doing too
         * much work on its main thread. */

        // Get recipe from activity
        DetailActivity x = (DetailActivity) getActivity();
        mRecipe = x.getRecipe();

        mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_detail_first_tab,
                container, false);


        // Set data
        TextView typeOfDishTextView = (TextView) mLayout.findViewById(R.id.type_of_dish);
        typeOfDishTextView.setText(UserFriendlyRecipeData.getTypeOfDishTranslation(mRecipe.getTypeOfDish(), getActivity()));

        TextView difficultyTextView = (TextView) mLayout.findViewById(R.id.difficulty);
        difficultyTextView.setText(UserFriendlyRecipeData.getDifficultyTranslation(mRecipe.getDifficulty(), getActivity()));

        TextView cookingTimeTextView = (TextView) mLayout.findViewById(R.id.cooking_time);
        cookingTimeTextView.setText(this.getCookingTime(mRecipe.getCookingTime()));

        TextView servingsTextView = (TextView) mLayout.findViewById(R.id.servings);
        servingsTextView.setText(Objects.toString(mRecipe.getServings()));

        TextView sourceTextView = (TextView) mLayout.findViewById(R.id.source);
        sourceTextView.setText(mRecipe.getSource());

        if (mRecipe.getSource().startsWith("http://") || mRecipe.getSource().startsWith("https://")) {
            sourceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchWebIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mRecipe.getSource()));
                    startActivity(launchWebIntent);
                }
            });
        }

        // Categories
        LinearLayout categoriesLayout = (LinearLayout) mLayout.findViewById(R.id.categories);

        for (int i = 0; i < mRecipe.getCategories().size(); i++) {
            RecipeCategory presentCategory = (RecipeCategory) mRecipe.getCategories().get(i);

            TextView categoryTextView = new TextView(getActivity());
            // TODO: Translate category name
            categoryTextView.setText(presentCategory.getName());
            categoryTextView.setTextColor(getResources().getColor(R.color.primary_text));

            categoriesLayout.addView(categoryTextView);
        }
        return mLayout;
    }


    // DATA USER-FRIENDLY

    /**
     * Translate cookingTime code to an understandable string
     *
     * @param time Float time
     * @return User-friendly string
     */
    private String getCookingTime(float time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);

        String completeTime = seconds + getString(R.string.detail_seconds);
        if (minutes > 0) {
            completeTime = minutes + getString(R.string.detail_minutes) + " " + completeTime;
        }

        return completeTime;
    }

}
