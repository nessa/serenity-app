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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.UserFriendlyRecipeData;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Recipe edition fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of add activity and edit activity.
 * It contains ingredients' data.
 *
 * Related layouts:
 * - Content: fragment_edition_second_tab.xml
 */
public class RecipeEditionSecondTabFragment extends Fragment {

    private EditionActivity mEditionActivity;

    private ArrayList<String> measurementUnits;

    private ArrayList<Pair<Long, RecipeIngredient>> mIngredientsArray;
    private RecipeEditionIngredientListAdapter mIngredientsListAdapter;


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

        measurementUnits = UserFriendlyRecipeData.getMeasurementUnits(getActivity());
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


        LinearLayout mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_edition_second_tab,
                container, false);

        mEditionActivity = (EditionActivity) getActivity();

        // Set initial ingredient list
        mIngredientsArray = new ArrayList<>();
        for (int i = 0; i < mEditionActivity.getRecipe().getIngredients().size(); i++) {
            RecipeIngredient ri = mEditionActivity.getRecipe().getIngredients().get(i);
            mIngredientsArray.add(new Pair<>(Long.valueOf(ri.getSortNumber()), ri));
        }

        // Set drag list view
        DragListView mIngredientsListView = (DragListView) mLayout.findViewById(R.id.ingredients);
        mIngredientsListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {}

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    moveIngredientsInRecipe(fromPosition, toPosition);
                }
            }
        });

        mIngredientsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIngredientsListAdapter = new RecipeEditionIngredientListAdapter(mIngredientsArray,
                R.layout.item_edition_ingredient, R.id.swap_image, false, getActivity(),
                RecipeEditionSecondTabFragment.this);
        mIngredientsListView.setAdapter(mIngredientsListAdapter, true);
        mIngredientsListView.setCanDragHorizontally(false);
        mIngredientsListView.setCustomDragItem(new IngredientDragItem(getActivity(),
                R.layout.item_edition_ingredient));


        return mLayout;
    }

    /**
     * Show new ingredient creation dialog.
     *
     * Uses showEditableDialog method.
     */
    public void showAddDialog() {
        Log.d("INFO", "SHOW ADD DIALOG");
        showEditionDialog(-1);
    }

    /**
     * Show ingredient edition dialog.
     * It creates a new ingredient or updates an existing one.
     * Modify ingredients in both recipe and mIngredientsArray.
     *
     * @param position Position of the ingredient to update.
     *                 If it's -1, it doesn't exist and we have
     *                 to create a new one.
     */
    public void showEditionDialog(final int position) {
        Log.d("INFO", "SHOW EDITION DIALOG");

        final Dialog editionDialog = new Dialog(getActivity());
        editionDialog.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        editionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editionDialog.setContentView(R.layout.dialog_edition_ingredient);

        TextView titleTextView = (TextView) editionDialog.findViewById(R.id.title);
        if (position > -1) {
            titleTextView.setText(getResources().getString(R.string.recipe_edition_ingredient_title_update));
        } else {
            titleTextView.setText(getResources().getString(R.string.recipe_edition_ingredient_title_new));
        }

        final TextView nameTextView = (TextView) editionDialog.findViewById(R.id.name);
        final TextView quantityTextView = (TextView) editionDialog.findViewById(R.id.quantity);

        final Spinner unitsSpinner = (Spinner) editionDialog.findViewById(R.id.measurement_units);
        ArrayAdapter<String> unitsSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, measurementUnits);
        unitsSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitsSpinnerArrayAdapter);

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
                // Validate fields
                if (nameTextView.getText().toString().length() > 0) {

                    // Calculate quantity and measurement unit
                    Float quantity = 0.0F;
                    String unit = UserFriendlyRecipeData.getDefaultMeasurementUnit();
                    if (quantityTextView.getText().toString().length() == 0) {
                        quantity = Float.valueOf(quantityTextView.getText().toString());
                        unit = UserFriendlyRecipeData.getMeasurementUnitTranslationByPosition(
                                unitsSpinner.getSelectedItemPosition(), getActivity());
                    }

                    if (position > -1) {
                        // Update existing ingredient
                        mEditionActivity.getRecipe().getIngredients().get(position).setName(
                                nameTextView.getText().toString());
                        mEditionActivity.getRecipe().getIngredients().get(position).setQuantity(quantity);
                        mEditionActivity.getRecipe().getIngredients().get(position).setMeasurementUnit(unit);

                        mIngredientsArray.get(position).second.setName(nameTextView.getText().toString());
                        mIngredientsArray.get(position).second.setQuantity(quantity);
                        mIngredientsArray.get(position).second.setMeasurementUnit(unit);
                    } else {
                        // Add new ingredient
                        RecipeIngredient ingredient = new RecipeIngredient(
                                mEditionActivity.getRecipe().getIngredients().size() + 1,
                                nameTextView.getText().toString(),
                                quantity,
                                unit
                        );

                        mEditionActivity.getRecipe().getIngredients().add(ingredient);
                        mIngredientsArray.add(new Pair<>(Long.valueOf(ingredient.getSortNumber()), ingredient));
                    }

                    mIngredientsListAdapter.notifyDataSetChanged();

                    // We close the dialog only if the creation/update result is OK
                    editionDialog.dismiss();
                }
            }
        });

        if (position > -1) {
            nameTextView.setText(mEditionActivity.getRecipe().getIngredients().get(position).getName());
            quantityTextView.setText(String.format("%f", mEditionActivity.getRecipe().getIngredients().get(position).getQuantity()));
            unitsSpinner.setSelection(UserFriendlyRecipeData.getMeasurementUnitPosition(
                    mEditionActivity.getRecipe().getIngredients().get(position).getMeasurementUnit()));
        }

        editionDialog.show();
    }

    /**
     * Remove ingredient from recipe and mIngredientsArray.
     *
     * @param position Position of the ingredient to remove.
     */
    public void removeIngredient(int position) {
        mEditionActivity.getRecipe().getIngredients().remove(position);
        mIngredientsArray.remove(position);

        // Reset sort numbers
        for (int i = 0; i < mEditionActivity.getRecipe().getIngredients().size(); i++) {
            mEditionActivity.getRecipe().getIngredients().get(i).setSortNumber(i + 1);
            mIngredientsArray.get(i).second.setSortNumber(i + 1);
        }

        mIngredientsListAdapter.notifyDataSetChanged();
    }

    /**
     * Move ingredients after drag and drop them in the list view.
     * We translate the ingredients movement from the list view
     * (mIngredientsArray) to the recipe.
     *
     * Do NOT change mIngredientsArray or the list view will show
     * a wrong animation.
     *
     * @param fromPosition Initial position of the ingredient to move.
     * @param toPosition Last position of the ingredient to move.
     */
    private void moveIngredientsInRecipe(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            Collections.rotate(mEditionActivity.getRecipe().getIngredients().subList(fromPosition, toPosition + 1), -1);
        } else {
            Collections.rotate(mEditionActivity.getRecipe().getIngredients().subList(toPosition, fromPosition + 1), +1);
        }

        // Reset sort numbers
        for (int i = 0; i < mEditionActivity.getRecipe().getIngredients().size(); i++) {
            mEditionActivity.getRecipe().getIngredients().get(i).setSortNumber(i + 1);
            mIngredientsArray.get(i).second.setSortNumber(i + 1);
        }

        mIngredientsListAdapter.notifyDataSetChanged();
    }

    /**
     * Drag item needed to populate the ingredients list view.
     */
    private static class IngredientDragItem extends DragItem {

        private Context mContext;

        public IngredientDragItem(Context context, int layoutId) {
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
            ((TextView) dragView.findViewById(R.id.quantity)).setText(((TextView) clickedView.findViewById(R.id.quantity)).getText());

            ((TextView) dragView.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            ((TextView) dragView.findViewById(R.id.quantity)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            dragView.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
            ((ImageView) dragView.findViewById(R.id.swap_image)).setColorFilter(mContext.getResources().getColor(android.R.color.white));
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.theme_default_primary));
        }
    }
}
