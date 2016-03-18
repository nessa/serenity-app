package com.amusebouche.amuseapp;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.data.UserFriendlyRecipeData;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;


import java.util.ArrayList;

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
public class RecipeEditionFirstTabFragment extends Fragment {


    private Recipe mRecipe;
    private LinearLayout mLayout;

    private ArrayList<String> typesOfDish;
    private ArrayList<String> difficulties;

    private EditText mTitle;
    private Spinner mTypeOfDish;
    private Spinner mDifficulty;
    private TextView mCookingTimeLabel;
    private SeekBar mCookingTimeHours;
    private SeekBar mCookingTimeMinutes;
    private TextView mServingsLabel;
    private SeekBar mServings;
    private DragListView mIngredientsListView;

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

        typesOfDish = new ArrayList<String>();
        typesOfDish.add(getString(R.string.type_of_dish_appetizer));
        typesOfDish.add(getString(R.string.type_of_dish_first_course));
        typesOfDish.add(getString(R.string.type_of_dish_second_course));
        typesOfDish.add(getString(R.string.type_of_dish_main_dish));
        typesOfDish.add(getString(R.string.type_of_dish_dessert));
        typesOfDish.add(getString(R.string.type_of_dish_other));

        difficulties = new ArrayList<String>();
        difficulties.add(getString(R.string.difficulty_low));
        difficulties.add(getString(R.string.difficulty_medium));
        difficulties.add(getString(R.string.difficulty_high));


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


        mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_edition_first_tab,
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

        mTitle = (EditText) mLayout.findViewById(R.id.recipe_title);
        mTitle.setText(mRecipe.getTitle());

        if (mTitle.getText().toString().length() == 0) {
            mTitle.setError(getString(R.string.recipe_edition_error_required));
        }
        mTitle.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if (mTitle.getText().toString().length() == 0) {
                    mTitle.setError(getString(R.string.recipe_edition_error_required));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        mTypeOfDish = (Spinner) mLayout.findViewById(R.id.type_of_dish);
        ArrayAdapter<String> typeOfDishSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, typesOfDish);
        typeOfDishSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeOfDish.setAdapter(typeOfDishSpinnerArrayAdapter);
        ///mTypeOfDish.setSelection();


        mDifficulty = (Spinner) mLayout.findViewById(R.id.difficulty);
        ArrayAdapter<String> difficultySpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, difficulties);
        difficultySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDifficulty.setAdapter(difficultySpinnerArrayAdapter);
        ///mDifficulty.setSelection();


        mCookingTimeLabel = (TextView) mLayout.findViewById(R.id.cooking_time);
        mCookingTimeHours = (SeekBar) mLayout.findViewById(R.id.cooking_time_hours);
        mCookingTimeMinutes = (SeekBar) mLayout.findViewById(R.id.cooking_time_minutes);
        setCookingTimeLabel();

        SeekBar.OnSeekBarChangeListener cookingTimeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setCookingTimeLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        mCookingTimeHours.setOnSeekBarChangeListener(cookingTimeListener);
        mCookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);


        // On saving recipe: servings = mServings.getProgress + 1
        mServingsLabel = (TextView) mLayout.findViewById(R.id.servings_label);
        mServings = (SeekBar) mLayout.findViewById(R.id.servings);
        setServingsLabel();

        mServings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setServingsLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return mLayout;
    }

    private void setCookingTimeLabel() {
        String time;

        if (mCookingTimeHours.getProgress() > 0) {
            if (mCookingTimeMinutes.getProgress() > 0) {
                time = String.format("%d %s - %d %s", mCookingTimeHours.getProgress(),
                        (mCookingTimeHours.getProgress() == 1) ? getString(R.string.detail_hour) : getString(R.string.detail_hours),
                        mCookingTimeMinutes.getProgress(),
                        (mCookingTimeMinutes.getProgress() == 1) ? getString(R.string.detail_minute) : getString(R.string.detail_minutes));
            } else {
                time = String.format("%d %s", mCookingTimeHours.getProgress(),
                        (mCookingTimeHours.getProgress() == 1) ? getString(R.string.detail_hour) : getString(R.string.detail_hours));
            }
        } else {
            time = String.format("%d %s", mCookingTimeMinutes.getProgress(),
                    (mCookingTimeMinutes.getProgress() == 1) ? getString(R.string.detail_minute) : getString(R.string.detail_minutes));
        }

        mCookingTimeLabel.setText(time);
    }

    private void setServingsLabel() {
        mServingsLabel.setText(String.format("%d %s", mServings.getProgress() + 1,
                (mServings.getProgress() == 0) ? getString(R.string.recipe_edition_serving) :
                        getString(R.string.recipe_edition_servings)));

    }


    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.theme_default_primary));
        }
    }
}
