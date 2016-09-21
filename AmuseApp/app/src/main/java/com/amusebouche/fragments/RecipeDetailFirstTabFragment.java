package com.amusebouche.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amusebouche.activities.DetailActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.services.UserFriendlyTranslationsHandler;

import java.util.concurrent.atomic.AtomicInteger;

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

    // Parent activity
    private DetailActivity mDetailActivity;

    // UI
    private LayoutInflater mInflater;
    private TextView mRecipeName;
    private TextView mRecipeOwner;
    private TextView mRecipeNumberUsersRating;
    private RatingBar mRatingBar;
    private TextView mTypeOfDishTextView;
    private TextView mDifficultyTextView;
    private TextView mCookingTimeTextView;
    private TextView mServingsTextView;
    private TextView mSourceTextView;
    private TextView mCategoriesTextView;
    private LinearLayout mCategoriesLayout;

    // Data
    private int maxWidth;
    private static int LATERAL_MARGIN = 10;
    private static final AtomicInteger TOP_BOTTOM_MARGIN = new AtomicInteger(5);


    // LIFECYCLE METHODS

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

        mDetailActivity = (DetailActivity)getActivity();

        // Get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        maxWidth = deviceDisplay.x - 2*LATERAL_MARGIN;

    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
        onReloadView();
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

        mInflater = inflater;

        View mLayout = inflater.inflate(R.layout.fragment_detail_first_tab, container, false);


        // Set UI elements
        mRecipeName = (TextView) mLayout.findViewById(R.id.recipe_name);
        mRecipeOwner = (TextView) mLayout.findViewById(R.id.recipe_owner);
        mRecipeNumberUsersRating = (TextView) mLayout.findViewById(R.id.recipe_number_users_rating);
        mRatingBar = (RatingBar) mLayout.findViewById(R.id.rating_bar);
        mTypeOfDishTextView = (TextView) mLayout.findViewById(R.id.type_of_dish);
        mDifficultyTextView = (TextView) mLayout.findViewById(R.id.difficulty);
        mCookingTimeTextView = (TextView) mLayout.findViewById(R.id.cooking_time);
        mServingsTextView = (TextView) mLayout.findViewById(R.id.servings);
        mSourceTextView = (TextView) mLayout.findViewById(R.id.source);
        mCategoriesTextView = (TextView) mLayout.findViewById(R.id.categories_label);
        mCategoriesLayout = (LinearLayout)  mLayout.findViewById(R.id.categories);

        return mLayout;
    }

    /**
     * Reload all UI elements with the recipe data.
     */
    public void onReloadView() {
        mRecipeName.setText(mDetailActivity.getRecipe().getTitle());
        if (mDetailActivity.getRecipe().getOwner().equals("")) {
            mRecipeOwner.setText(getString(R.string.detail_user_you));
        } else {
            mRecipeOwner.setText(mDetailActivity.getRecipe().getOwner());
        }

        float rating = 0;
        if (mDetailActivity.getRecipe().getUsersRating() > 0) {
            rating = mDetailActivity.getRecipe().getTotalRating() / mDetailActivity.getRecipe().getUsersRating();
        }
        mRatingBar.setRating(rating);

        mRecipeNumberUsersRating.setText(UserFriendlyTranslationsHandler.getUsersLabel(
            mDetailActivity.getRecipe().getUsersRating(), mDetailActivity));

        mTypeOfDishTextView.setText(UserFriendlyTranslationsHandler.getTypeOfDishTranslation(
            mDetailActivity.getRecipe().getTypeOfDish(), getActivity()));
        mDifficultyTextView.setText(UserFriendlyTranslationsHandler.getDifficultyTranslation(
            mDetailActivity.getRecipe().getDifficulty(), getActivity()));
        mCookingTimeTextView.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
            mDetailActivity.getRecipe().getCookingTime().intValue() / 60,
            mDetailActivity.getRecipe().getCookingTime().intValue() % 60,
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
        boolean categoriesVisible = mDetailActivity.getRecipe().getCategories().size() > 0;
        if (mDetailActivity.getRecipe().getCategories().size() == 1) {
            categoriesVisible = !mDetailActivity.getRecipe().getCategories().get(0).getName().equals(
                    RecipeCategory.CATEGORY_UNCATEGORIZED);
        }

        mCategoriesTextView.setVisibility(categoriesVisible ? View.VISIBLE : View.GONE);

        mCategoriesLayout.removeAllViews();
        mCategoriesLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(getActivity());
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.START);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0; i < mDetailActivity.getRecipe().getCategories().size(); i++) {
            Log.d("FIRST", "CATEGORY "+mDetailActivity.getRecipe().getCategories().get(i).getName());
            if (!mDetailActivity.getRecipe().getCategories().get(i).getName().equals(
                    RecipeCategory.CATEGORY_UNCATEGORIZED)) {
                LinearLayout LL = new LinearLayout(getActivity());
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                //Create new tag view
                View tagView = mInflater.inflate(R.layout.item_detail_category, mCategoriesLayout, false);

                TextView tagText = (TextView) tagView.findViewById(R.id.text);
                tagText.setText(UserFriendlyTranslationsHandler.getCategoryTranslation(
                        mDetailActivity.getRecipe().getCategories().get(i).getName(),
                        getActivity()).toUpperCase());

                tagView.measure(0, 0);

                params = new LinearLayout.LayoutParams(tagView.getMeasuredWidth(),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(LATERAL_MARGIN, TOP_BOTTOM_MARGIN.get(), LATERAL_MARGIN, TOP_BOTTOM_MARGIN.get());

                LL.addView(tagView, params);
                LL.measure(0, 0);
                widthSoFar += tagView.getMeasuredWidth();
                if (widthSoFar >= maxWidth) {
                    mCategoriesLayout.addView(newLL);

                    newLL = new LinearLayout(getActivity());
                    newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    newLL.setOrientation(LinearLayout.HORIZONTAL);
                    newLL.setGravity(Gravity.START);
                    params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
                    newLL.addView(LL, params);
                    widthSoFar = LL.getMeasuredWidth();
                } else {
                    newLL.addView(LL);
                }
            }
        }

        mCategoriesLayout.addView(newLL);
    }
}
