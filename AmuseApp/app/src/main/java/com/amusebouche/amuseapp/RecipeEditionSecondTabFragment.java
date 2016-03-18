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
 * Recipe edtion fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of add activity and edit activity.
 * It contains lots of inputs to edit recipe's data.
 *
 * Related layouts:
 * - Content: fragment_edition_first_tab.xmlxml
 */
public class RecipeEditionSecondTabFragment extends Fragment {

    private AddActivity mAddActivity;

    private ArrayList<String> measurementUnits;

    private ArrayList<Pair<Long, RecipeIngredient>> mItemArray;


    private RecipeEditionListAdapter mIngredientsListAdapter;

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

        if (getActivity() instanceof AddActivity) {
            // do something
            mAddActivity = (AddActivity) getActivity();
        } else {
            //do something else
            Log.d("INFO", "ELSE");
        }

        // Test

        mItemArray = new ArrayList<>();

        if (mAddActivity != null) {
            for (int i = 0; i < mAddActivity.getRecipe().getIngredients().size(); i++) {
                RecipeIngredient ri = mAddActivity.getRecipe().getIngredients().get(i);
                mItemArray.add(new Pair<>(Long.valueOf(ri.getSortNumber()), ri));
            }
        }


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
        mIngredientsListAdapter = new RecipeEditionListAdapter(mItemArray,
                R.layout.item_edition_ingredient, R.id.image, false, getActivity());
        mIngredientsListView.setAdapter(mIngredientsListAdapter, true);
        mIngredientsListView.setCanDragHorizontally(false);
        mIngredientsListView.setCustomDragItem(new MyDragItem(getActivity(),
                R.layout.item_edition_ingredient));


        return mLayout;
    }

    public void reloadItems() {
        mItemArray.clear();

        if (mAddActivity != null) {
            for (int i = 0; i < mAddActivity.getRecipe().getIngredients().size(); i++) {
                mItemArray.add(new Pair<>(Long.valueOf(mAddActivity.getRecipe().getIngredients().get(i).getSortNumber()),
                        mAddActivity.getRecipe().getIngredients().get(i)));
            }

            mIngredientsListAdapter.notifyDataSetChanged();
        }

    }

    public void showAddDialog() {
        Log.d("INFO", "SHOW ADD DIALOG");
        showEditionDialog(-1);
    }

    private void showEditionDialog(final int position) {
        Log.d("INFO", "SHOW EDITION DIALOG");

        final Dialog editionDialog = new Dialog(getActivity());
        editionDialog.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
        editionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editionDialog.setContentView(R.layout.dialog_edition_ingredient);

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
                editionDialog.dismiss();

                if (nameTextView.getText().toString().length() > 0 &&
                        quantityTextView.getText().toString().length() > 0) {
                    if (position > -1) {
                        // Update existing ingredient
                        mAddActivity.getRecipe().getIngredients().get(position).setName(nameTextView.getText().toString());
                        mAddActivity.getRecipe().getIngredients().get(position).setQuantity(
                                Float.valueOf(quantityTextView.getText().toString()));
                        mAddActivity.getRecipe().getIngredients().get(position).setMeasurementUnit(
                                UserFriendlyRecipeData.getMeasurementUnitTranslationByPosition(
                                        unitsSpinner.getSelectedItemPosition(), getActivity()));

                    } else {

                        // Add new ingredient
                        mAddActivity.getRecipe().getIngredients().add(new RecipeIngredient(
                                mAddActivity.getRecipe().getIngredients().size(),
                                nameTextView.getText().toString(),
                                Float.valueOf(quantityTextView.getText().toString()),
                                UserFriendlyRecipeData.getMeasurementUnitTranslationByPosition(
                                        unitsSpinner.getSelectedItemPosition(), getActivity())
                        ));
                    }

                    reloadItems();
                }
            }
        });

        if (position > -1) {

            if (mAddActivity != null) {
                nameTextView.setText(mAddActivity.getRecipe().getIngredients().get(position).getName());
                quantityTextView.setText(String.format("%f", mAddActivity.getRecipe().getIngredients().get(position).getQuantity()));

            }
        }

        editionDialog.show();
    }

    private void moveIngredientsInRecipe(int fromPosition, int toPosition) {
        if (mAddActivity != null) {
            if (fromPosition < toPosition) {
                Collections.rotate(mAddActivity.getRecipe().getIngredients().subList(fromPosition, toPosition + 1), -1);
            } else {
                Collections.rotate(mAddActivity.getRecipe().getIngredients().subList(toPosition, fromPosition + 1), +1);
            }

            for (int i = 0; i < mAddActivity.getRecipe().getIngredients().size(); i++) {
                mAddActivity.getRecipe().getIngredients().get(i).setSortNumber(i);
            }

            reloadItems();
        }
    }

    private static class MyDragItem extends DragItem {

        private Context mContext;

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
            mContext = context;
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            ((TextView) dragView.findViewById(R.id.name)).setText(((TextView) clickedView.findViewById(R.id.name)).getText());
            ((TextView) dragView.findViewById(R.id.quantity)).setText(((TextView) clickedView.findViewById(R.id.quantity)).getText());

            ((TextView) dragView.findViewById(R.id.name)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            ((TextView) dragView.findViewById(R.id.quantity)).setTextColor(mContext.getResources().getColor(android.R.color.white));
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.theme_default_primary));
        }
    }
}
