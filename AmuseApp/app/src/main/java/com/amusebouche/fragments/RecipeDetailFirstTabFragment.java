package com.amusebouche.fragments;

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
import android.widget.TextView;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.services.UserFriendlyTranslationsHandler;
import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;

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

    // Father activity
    private DetailActivity mDetailActivity;

    // UI
    private TextView mTypeOfDishTextView;
    private TextView mDifficultyTextView;
    private TextView mCookingTimeTextView;
    private TextView mServingsTextView;
    private TextView mSourceTextView;
    private TextView mCategoriesTextView;
    private TagCloudLinkView mCategoriesLayout;


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

        // Get recipe from activity
        mDetailActivity = (DetailActivity) getActivity();

        FrameLayout mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_detail_first_tab,
            container, false);

        // Set UI elements
        mTypeOfDishTextView = (TextView) mLayout.findViewById(R.id.type_of_dish);
        mDifficultyTextView = (TextView) mLayout.findViewById(R.id.difficulty);
        mCookingTimeTextView = (TextView) mLayout.findViewById(R.id.cooking_time);
        mServingsTextView = (TextView) mLayout.findViewById(R.id.servings);
        mSourceTextView = (TextView) mLayout.findViewById(R.id.source);
        mCategoriesTextView = (TextView) mLayout.findViewById(R.id.categories_label);
        mCategoriesLayout = (TagCloudLinkView)  mLayout.findViewById(R.id.categories);

        onReloadView();

        return mLayout;
    }

    /**
     * Reload all UI elements with the mRecipe data.
     */
    public void onReloadView() {

        mTypeOfDishTextView.setText(UserFriendlyTranslationsHandler.getTypeOfDishTranslation(
            mDetailActivity.getRecipe().getTypeOfDish(), getActivity()));
        mDifficultyTextView.setText(UserFriendlyTranslationsHandler.getDifficultyTranslation(
            mDetailActivity.getRecipe().getDifficulty(), getActivity()));
        mCookingTimeTextView.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
            mDetailActivity.getRecipe().getCookingTime().intValue() / 60, mDetailActivity.getRecipe().getCookingTime().intValue() % 60,
            getActivity()));
        mServingsTextView.setText(UserFriendlyTranslationsHandler.getServingsLabel(
            mDetailActivity.getRecipe().getServings(), getActivity()));

        // Source
        mSourceTextView.setText(mDetailActivity.getRecipe().getSource());

        if (mDetailActivity.getRecipe().getSource().startsWith("http://") ||
            mDetailActivity.getRecipe().getSource().startsWith("https://")) {

            mSourceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchWebIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mDetailActivity.getRecipe().getSource()));
                    startActivity(launchWebIntent);
                }
            });
        }

        // Categories
        mCategoriesTextView.setVisibility(mDetailActivity.getRecipe().getCategories().size() > 0 ? View.VISIBLE : View.GONE);

        mCategoriesLayout.removeAllViews();
        for (int i = 0; i < mDetailActivity.getRecipe().getCategories().size(); i++) {
            if (!mDetailActivity.getRecipe().getCategories().get(i).getName().equals(RecipeCategory.CATEGORY_UNCATEGORIZED)) {
                mCategoriesLayout.add(new Tag(i, UserFriendlyTranslationsHandler.getCategoryTranslation(
                    mDetailActivity.getRecipe().getCategories().get(i).getName(), getActivity()).toUpperCase()));
            }
        }
    }

}
