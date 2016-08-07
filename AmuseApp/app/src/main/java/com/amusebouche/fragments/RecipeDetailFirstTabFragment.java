package com.amusebouche.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    // UI
    private SimpleRecyclerAdapter mAdapter;

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

        RecyclerView mLayout = (RecyclerView) inflater.inflate(R.layout.fragment_detail_first_tab,
            container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mLayout.setLayoutManager(linearLayoutManager);

        mAdapter = new SimpleRecyclerAdapter(getActivity(), inflater);
        mLayout.setAdapter(mAdapter);

        onReloadView();


        return mLayout;
    }

    /**
     * Reload all UI elements with the recipe data.
     */
    public void onReloadView() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Simple adapter for a recycler view that shows the detail data
     */
    public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

        private DetailActivity mContext;
        private LayoutInflater mInflater;

        public SimpleRecyclerAdapter(Context context, LayoutInflater inflater) {
            this.mContext = (DetailActivity) context;
            this.mInflater = inflater;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

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

            public ViewHolder(final View view) {
                super(view);

                // Set UI elements
                mRecipeName = (TextView) view.findViewById(R.id.recipe_name);
                mRecipeOwner = (TextView) view.findViewById(R.id.recipe_owner);
                mRecipeNumberUsersRating = (TextView) view.findViewById(R.id.recipe_number_users_rating);
                mRatingBar = (RatingBar) view.findViewById(R.id.rating_bar);
                mTypeOfDishTextView = (TextView) view.findViewById(R.id.type_of_dish);
                mDifficultyTextView = (TextView) view.findViewById(R.id.difficulty);
                mCookingTimeTextView = (TextView) view.findViewById(R.id.cooking_time);
                mServingsTextView = (TextView) view.findViewById(R.id.servings);
                mSourceTextView = (TextView) view.findViewById(R.id.source);
                mCategoriesTextView = (TextView) view.findViewById(R.id.categories_label);
                mCategoriesLayout = (LinearLayout)  view.findViewById(R.id.categories);
            }
        }

        @Override
        public SimpleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_detail_first_tab_content, parent, false);

            // create ViewHolder
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mRecipeName.setText(mContext.getRecipe().getTitle());
            holder.mRecipeOwner.setText(mContext.getRecipe().getOwner());

            float rating = 0;
            if (mContext.getRecipe().getUsersRating() > 0) {
                rating = mContext.getRecipe().getTotalRating() / mContext.getRecipe().getUsersRating();
            }
            holder.mRatingBar.setRating(rating);

            holder.mRecipeNumberUsersRating.setText(UserFriendlyTranslationsHandler.getUsersLabel(
                mContext.getRecipe().getUsersRating(), mContext));

            LayerDrawable stars = (LayerDrawable) holder.mRatingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getResources().getColor(android.R.color.white),
                PorterDuff.Mode.SRC_ATOP);

            holder.mTypeOfDishTextView.setText(UserFriendlyTranslationsHandler.getTypeOfDishTranslation(
                mContext.getRecipe().getTypeOfDish(), getActivity()));
            holder.mDifficultyTextView.setText(UserFriendlyTranslationsHandler.getDifficultyTranslation(
                mContext.getRecipe().getDifficulty(), getActivity()));
            holder.mCookingTimeTextView.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
                mContext.getRecipe().getCookingTime().intValue() / 60,
                mContext.getRecipe().getCookingTime().intValue() % 60,
                getActivity()));
            holder.mServingsTextView.setText(UserFriendlyTranslationsHandler.getServingsLabel(
                mContext.getRecipe().getServings(), getActivity()));

            // Source
            holder.mSourceTextView.setText(mContext.getRecipe().getSource());

            if (mContext.getRecipe().getSource().startsWith("http://") ||
                mContext.getRecipe().getSource().startsWith("https://")) {

                holder.mSourceTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent launchWebIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mContext.getRecipe().getSource()));
                        startActivity(launchWebIntent);
                    }
                });
            }

            // Categories
            holder.mCategoriesTextView.setVisibility((mContext.getRecipe().getCategories().size() > 0) ?
                View.VISIBLE : View.GONE);

            holder.mCategoriesLayout.removeAllViews();
            holder.mCategoriesLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params;
            LinearLayout newLL = new LinearLayout(getActivity());
            newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
            newLL.setGravity(Gravity.START);
            newLL.setOrientation(LinearLayout.HORIZONTAL);

            int widthSoFar = 0;

            for (int i = 0; i < mContext.getRecipe().getCategories().size(); i++) {
                LinearLayout LL = new LinearLayout(getActivity());
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

                //Create new tag view
                View tagView = mInflater.inflate(R.layout.item_detail_category,
                    holder.mCategoriesLayout, false);

                TextView tagText = (TextView) tagView.findViewById(R.id.text);
                tagText.setText(UserFriendlyTranslationsHandler.getCategoryTranslation(
                    mContext.getRecipe().getCategories().get(i).getName(),
                    getActivity()).toUpperCase());

                tagView.measure(0, 0);

                params = new LinearLayout.LayoutParams(tagView.getMeasuredWidth(),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(LATERAL_MARGIN, TOP_BOTTOM_MARGIN.get(), LATERAL_MARGIN, TOP_BOTTOM_MARGIN.get());

                LL.addView(tagView, params);
                LL.measure(0, 0);
                widthSoFar += tagView.getMeasuredWidth();
                if (widthSoFar >= maxWidth) {
                    holder.mCategoriesLayout.addView(newLL);

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

            holder.mCategoriesLayout.addView(newLL);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

}
