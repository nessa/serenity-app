package com.amusebouche.amuseapp;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amusebouche.data.RecipeDirection;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Recipe edition fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of add activity and edit activity.
 * It contains directions' data.
 *
 * Related layouts:
 * - Content: fragment_edition_third_tab.xml
 */
public class RecipeEditionThirdTabFragment extends Fragment {

    private EditionActivity mEditionActivity;

    private ArrayList<Pair<Long, RecipeDirection>> mDirectionsArray;
    private RecipeEditionDirectionListAdapter mDirectionsListAdapter;


    // LIFECYCLE METHODS

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


        LinearLayout mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_edition_third_tab,
                container, false);

        mEditionActivity = (EditionActivity) getActivity();

        // Set initial direction list
        mDirectionsArray = new ArrayList<>();
        for (int i = 0; i < mEditionActivity.getRecipe().getDirections().size(); i++) {
            RecipeDirection rd = mEditionActivity.getRecipe().getDirections().get(i);
            mDirectionsArray.add(new Pair<>(Long.valueOf(rd.getSortNumber()), rd));
        }

        // Set drag list view
        DragListView mDirectionsListView = (DragListView) mLayout.findViewById(R.id.directions);
        mDirectionsListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    moveDirectionsInRecipe(fromPosition, toPosition);
                }
            }
        });

        mDirectionsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDirectionsListAdapter = new RecipeEditionDirectionListAdapter(mDirectionsArray,
                R.layout.item_edition_direction, R.id.swap_image, false, getActivity(),
                RecipeEditionThirdTabFragment.this);
        mDirectionsListView.setAdapter(mDirectionsListAdapter, true);
        mDirectionsListView.setCanDragHorizontally(false);
        mDirectionsListView.setCustomDragItem(new DirectionDragItem(getActivity(),
                R.layout.item_edition_direction));


        return mLayout;
    }

    /**
     * Show new direction creation dialog.
     *
     * Uses showEditableDialog method.
     */
    public void showAddDialog() {
        Log.d("INFO", "SHOW ADD DIALOG");
        showEditionDialog(-1);
    }

    /**
     * Show direction edition dialog.
     * It creates a new direction or updates an existing one.
     * Modify directions in both recipe and mDirectionsArray.
     *
     * @param position Position of the direction to update.
     *                 If it's -1, it doesn't exist and we have
     *                 to create a new one.
     */
    public void showEditionDialog(final int position) {
        Log.d("INFO", "SHOW EDITION DIALOG");

        final Dialog editionDialog = new Dialog(getActivity());
        editionDialog.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        editionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editionDialog.setContentView(R.layout.dialog_edition_direction);

        final TextView descriptionTextView = (TextView) editionDialog.findViewById(R.id.description);
        final TextView imageTextView = (TextView) editionDialog.findViewById(R.id.image);
        final TextView videoTextView = (TextView) editionDialog.findViewById(R.id.video);

        // Cooking time
        final TextView cookingTimeLabel = (TextView) editionDialog.findViewById(R.id.cooking_time);
        final SeekBar cookingTimeHours = (SeekBar) editionDialog.findViewById(R.id.cooking_time_hours);
        final SeekBar cookingTimeMinutes = (SeekBar) editionDialog.findViewById(R.id.cooking_time_minutes);

        SeekBar.OnSeekBarChangeListener cookingTimeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cookingTimeLabel.setText(getCookingTimeLabel(cookingTimeHours.getProgress(),
                        cookingTimeMinutes.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        cookingTimeHours.setOnSeekBarChangeListener(cookingTimeListener);
        cookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);

        Button cancelButton = (Button) editionDialog.findViewById(R.id.cancel);
        Button acceptButton = (Button) editionDialog.findViewById(R.id.accept);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editionDialog.dismiss();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (descriptionTextView.getText().toString().length() > 0) {

                    if (position > -1) {
                        // Update existing direction
                        mEditionActivity.getRecipe().getDirections().get(position).setDescription(
                                descriptionTextView.getText().toString());
                        mEditionActivity.getRecipe().getDirections().get(position).setImage(
                                imageTextView.getText().toString());
                        mEditionActivity.getRecipe().getDirections().get(position).setVideo(
                                videoTextView.getText().toString());
                        mEditionActivity.getRecipe().getDirections().get(position).setTime(
                                cookingTimeHours.getProgress() * 60.0F +
                                        cookingTimeMinutes.getProgress());
                    } else {
                        // Add new direction
                        RecipeDirection direction = new RecipeDirection(
                                mEditionActivity.getRecipe().getDirections().size() + 1,
                                descriptionTextView.getText().toString(),
                                imageTextView.getText().toString(),
                                videoTextView.getText().toString(),
                                cookingTimeHours.getProgress() * 60.0F + cookingTimeMinutes.getProgress());

                        mEditionActivity.getRecipe().getDirections().add(direction);
                        mDirectionsArray.add(new Pair<>(Long.valueOf(direction.getSortNumber()), direction));
                    }

                    mDirectionsListAdapter.notifyDataSetChanged();

                    // We close the dialog only if the creation/update result is OK
                    editionDialog.dismiss();
                }
            }
        });

        if (position > -1) {
            descriptionTextView.setText(mEditionActivity.getRecipe().getDirections().get(position).getDescription());
            imageTextView.setText(mEditionActivity.getRecipe().getDirections().get(position).getImage());
            videoTextView.setText(mEditionActivity.getRecipe().getDirections().get(position).getVideo());            cookingTimeHours.setProgress(mEditionActivity.getRecipe().getCookingTime().intValue() / 60);
            cookingTimeMinutes.setProgress(mEditionActivity.getRecipe().getCookingTime().intValue() % 60);

            cookingTimeLabel.setText(getCookingTimeLabel(cookingTimeHours.getProgress(),
                    cookingTimeMinutes.getProgress()));
        }

        editionDialog.show();
    }


    private String getCookingTimeLabel(Integer cookingHours, Integer cookingMinutes) {
        if (cookingHours > 0) {
            if (cookingMinutes > 0) {
                return String.format("%d %s - %d %s", cookingHours,
                        (cookingHours == 1) ? getString(R.string.detail_hour) : getString(R.string.detail_hours),
                        cookingMinutes,
                        (cookingMinutes == 1) ? getString(R.string.detail_minute) : getString(R.string.detail_minutes));
            } else {
                return String.format("%d %s", cookingHours,
                        (cookingHours == 1) ? getString(R.string.detail_hour) : getString(R.string.detail_hours));
            }
        } else {
            return String.format("%d %s", cookingMinutes,
                    (cookingMinutes == 1) ? getString(R.string.detail_minute) : getString(R.string.detail_minutes));
        }
    }

    /**
     * Remove direction from recipe and mDirectionsArray.
     *
     * @param position Position of the direction to remove.
     */
    public void removeDirection(int position) {
        mEditionActivity.getRecipe().getDirections().remove(position);
        mDirectionsArray.remove(position);

        // Reset sort numbers
        for (int i = 0; i < mEditionActivity.getRecipe().getDirections().size(); i++) {
            mEditionActivity.getRecipe().getDirections().get(i).setSortNumber(i + 1);
            mDirectionsArray.get(i).second.setSortNumber(i + 1);
        }

        mDirectionsListAdapter.notifyDataSetChanged();
    }

    /**
     * Move directions after drag and drop them in the list view.
     * We translate the directions movement from the list view
     * (mDirectionsArray) to the recipe.
     *
     * Do NOT change mDirectionsArray or the list view will show
     * a wrong animation.
     *
     * @param fromPosition Initial position of the direction to move.
     * @param toPosition Last position of the direction to move.
     */
    private void moveDirectionsInRecipe(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            Collections.rotate(mEditionActivity.getRecipe().getDirections().subList(fromPosition, toPosition + 1), -1);
        } else {
            Collections.rotate(mEditionActivity.getRecipe().getDirections().subList(toPosition, fromPosition + 1), +1);
        }

        // Reset sort numbers
        for (int i = 0; i < mEditionActivity.getRecipe().getDirections().size(); i++) {
            mEditionActivity.getRecipe().getDirections().get(i).setSortNumber(i + 1);
            mDirectionsArray.get(i).second.setSortNumber(i + 1);
        }

        mDirectionsListAdapter.notifyDataSetChanged();
    }

    /**
     * Drag item needed to populate the directions list view.
     */
    private static class DirectionDragItem extends DragItem {

        private Context mContext;

        public DirectionDragItem(Context context, int layoutId) {
            super(context, layoutId);
            mContext = context;
        }

        /**
         * Fill dragged view with real data and change its style.
         *
         * @param clickedView Original view to get the data.
         * @param dragView Dragged view to modify.
         */
        @Override
        public void onBindDragView(View clickedView, View dragView) {
            ((TextView) dragView.findViewById(R.id.name)).setText(((TextView) clickedView.findViewById(R.id.name)).getText());
            ((TextView) dragView.findViewById(R.id.description)).setText(((TextView) clickedView.findViewById(R.id.description)).getText());
            dragView.findViewById(R.id.image_icon).setVisibility(clickedView.findViewById(R.id.image_icon).getVisibility());
            dragView.findViewById(R.id.video_icon).setVisibility(clickedView.findViewById(R.id.video_icon).getVisibility());
            dragView.findViewById(R.id.timer_icon).setVisibility(clickedView.findViewById(R.id.timer_icon).getVisibility());

            ((TextView) dragView.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            ((TextView) dragView.findViewById(R.id.description)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            dragView.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
            ((ImageView) dragView.findViewById(R.id.swap_image)).setColorFilter(mContext.getResources().getColor(android.R.color.white));
            ((ImageView) dragView.findViewById(R.id.image_icon)).setColorFilter(mContext.getResources().getColor(android.R.color.white));
            ((ImageView) dragView.findViewById(R.id.video_icon)).setColorFilter(mContext.getResources().getColor(android.R.color.white));
            ((ImageView) dragView.findViewById(R.id.timer_icon)).setColorFilter(mContext.getResources().getColor(android.R.color.white));
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.theme_default_primary));
        }
    }
}
