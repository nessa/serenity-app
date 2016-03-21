package com.amusebouche.amuseapp;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.amusebouche.data.UserFriendlyRecipeData;


import java.util.ArrayList;

/**
 * Recipe edition fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of add activity and edit activity.
 * It contains lots of inputs to edit recipe's data.
 *
 * Related layouts:
 * - Content: fragment_edition_first_tab.xml
 */
public class RecipeEditionFirstTabFragment extends Fragment {

    private EditionActivity mEditionActivity;

    private ArrayList<String> typesOfDish;
    private ArrayList<String> difficulties;

    private EditText mTitle;
    private EditText mImage;
    private TextView mCookingTimeLabel;
    private SeekBar mCookingTimeHours;
    private SeekBar mCookingTimeMinutes;
    private TextView mServingsLabel;
    private SeekBar mServings;
    private EditText mSource;

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
        super.onCreate(savedInstanceState);

        typesOfDish = UserFriendlyRecipeData.getTypes(getActivity());
        difficulties = UserFriendlyRecipeData.getDifficulties(getActivity());
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


        ScrollView mLayout = (ScrollView) inflater.inflate(R.layout.fragment_edition_first_tab,
                container, false);

        mEditionActivity = (EditionActivity) getActivity();

        // Title
        mTitle = (EditText) mLayout.findViewById(R.id.title);

        mTitle.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mEditionActivity.getRecipe().setTitle(mTitle.getText().toString());

                toggleEnableSaveButton(mEditionActivity.checkRequiredValidation(mTitle) &&
                        mEditionActivity.checkURLValidation(mImage));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Image
        mImage = (EditText) mLayout.findViewById(R.id.image);

        mImage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mEditionActivity.getRecipe().setImage(mImage.getText().toString());

                toggleEnableSaveButton(mEditionActivity.checkRequiredValidation(mTitle) &&
                        mEditionActivity.checkURLValidation(mImage));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Type of dish
        Spinner mTypeOfDish = (Spinner) mLayout.findViewById(R.id.type_of_dish);
        ArrayAdapter<String> typeOfDishSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, typesOfDish);
        typeOfDishSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeOfDish.setAdapter(typeOfDishSpinnerArrayAdapter);

        mTypeOfDish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mEditionActivity.getRecipe().setTypeOfDish(
                        UserFriendlyRecipeData.getTypeOfDishTranslationByPosition(position, getActivity()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Difficulty
        Spinner mDifficulty = (Spinner) mLayout.findViewById(R.id.difficulty);
        ArrayAdapter<String> difficultySpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, difficulties);
        difficultySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDifficulty.setAdapter(difficultySpinnerArrayAdapter);

        mDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mEditionActivity.getRecipe().setDifficulty(
                        UserFriendlyRecipeData.getDifficultyTranslationByPosition(position, getActivity()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Cooking time
        mCookingTimeLabel = (TextView) mLayout.findViewById(R.id.cooking_time);
        mCookingTimeHours = (SeekBar) mLayout.findViewById(R.id.cooking_time_hours);
        mCookingTimeMinutes = (SeekBar) mLayout.findViewById(R.id.cooking_time_minutes);

        SeekBar.OnSeekBarChangeListener cookingTimeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCookingTimeLabel.setText(UserFriendlyRecipeData.getCookingTimeLabel(mCookingTimeHours.getProgress(),
                        mCookingTimeMinutes.getProgress(), getActivity()));

                mEditionActivity.getRecipe().setCookingTime(mCookingTimeHours.getProgress() * 60.0F +
                        mCookingTimeMinutes.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        mCookingTimeHours.setOnSeekBarChangeListener(cookingTimeListener);
        mCookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);

        // Servings
        // On saving recipe: servings = mServings.getProgress + 1
        mServingsLabel = (TextView) mLayout.findViewById(R.id.servings_label);
        mServings = (SeekBar) mLayout.findViewById(R.id.servings);

        mServings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setServingsLabel();

                mEditionActivity.getRecipe().setServings(mServings.getProgress() + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mSource = (EditText) mLayout.findViewById(R.id.source);

        mSource.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mEditionActivity.getRecipe().setSource(mSource.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        // Set initial data
        mTitle.setText(mEditionActivity.getRecipe().getTitle());
        mImage.setText(mEditionActivity.getRecipe().getImage());
        mTypeOfDish.setSelection(UserFriendlyRecipeData.getTypeOfDishPosition(mEditionActivity.getRecipe().getTypeOfDish()));
        mDifficulty.setSelection(UserFriendlyRecipeData.getDifficultyPosition(mEditionActivity.getRecipe().getDifficulty()));
        mCookingTimeHours.setProgress(mEditionActivity.getRecipe().getCookingTime().intValue() / 60);
        mCookingTimeMinutes.setProgress(mEditionActivity.getRecipe().getCookingTime().intValue() % 60);
        mServings.setProgress((mEditionActivity.getRecipe().getServings() > 0) ?
                mEditionActivity.getRecipe().getServings() - 1 : 0);
        mSource.setText(mEditionActivity.getRecipe().getSource());

        // Check initial data
        setServingsLabel();

        mCookingTimeLabel.setText(UserFriendlyRecipeData.getCookingTimeLabel(mCookingTimeHours.getProgress(),
                mCookingTimeMinutes.getProgress(), getActivity()));

        toggleEnableSaveButton(mEditionActivity.checkRequiredValidation(mTitle) &&
                mEditionActivity.checkURLValidation(mImage));

        return mLayout;
    }

    private void toggleEnableSaveButton(boolean enabled) {
        mEditionActivity.setEnableSaveButton(enabled);
    }

    private void setServingsLabel() {
        mServingsLabel.setText(String.format("%d %s", mServings.getProgress() + 1,
                (mServings.getProgress() == 0) ? getString(R.string.recipe_edition_serving) :
                        getString(R.string.recipe_edition_servings)));

    }

}
