package com.amusebouche.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.amusebouche.activities.EditionActivity;
import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeCategory;
import com.amusebouche.services.UserFriendlyTranslationsHandler;


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

    // Parent activity
    private EditionActivity mEditionActivity;

    // UI
    private LayoutInflater mInflater;
    private EditText mTitle;
    private EditText mImage;
    private TextView mCookingTimeLabel;
    private SeekBar mCookingTimeHours;
    private SeekBar mCookingTimeMinutes;
    private TextView mServingsLabel;
    private Spinner mTypeOfDish;
    private Spinner mDifficulty;
    private SeekBar mServings;
    private EditText mSource;
    private LinearLayout mCategories;

    // Data variables
    private ArrayList<String> categories;
    private ArrayList<String> typesOfDish;
    private ArrayList<String> difficulties;

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

        mEditionActivity = (EditionActivity)getActivity();

        categories = UserFriendlyTranslationsHandler.getCategories(getActivity());
        typesOfDish = UserFriendlyTranslationsHandler.getTypes(getActivity());
        difficulties = UserFriendlyTranslationsHandler.getDifficulties(getActivity());
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

        mInflater = inflater;

        View mLayout = inflater.inflate(R.layout.fragment_edition_first_tab, container, false);

        // Set UI elements
        mTitle = (EditText) mLayout.findViewById(R.id.title);
        mImage = (EditText) mLayout.findViewById(R.id.image);
        mTypeOfDish = (Spinner) mLayout.findViewById(R.id.type_of_dish);
        mDifficulty = (Spinner) mLayout.findViewById(R.id.difficulty);
        mCookingTimeLabel = (TextView) mLayout.findViewById(R.id.cooking_time);
        mCookingTimeHours = (SeekBar) mLayout.findViewById(R.id.cooking_time_hours);
        mCookingTimeMinutes = (SeekBar) mLayout.findViewById(R.id.cooking_time_minutes);
        mServingsLabel = (TextView) mLayout.findViewById(R.id.servings_label);
        mServings = (SeekBar) mLayout.findViewById(R.id.servings);
        mSource = (EditText) mLayout.findViewById(R.id.source);
        mCategories = (LinearLayout) mLayout.findViewById(R.id.categories);

        // Set listeners

        // Title
        mTitle.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mEditionActivity.getRecipe().setTitle(mTitle.getText().toString());

                mEditionActivity.checkRequiredValidation(mTitle);

                mEditionActivity.checkEnableSaveButton();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Image
        mImage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mEditionActivity.getRecipe().setImage(mImage.getText().toString());

                mEditionActivity.checkURLValidation(mImage);

                mEditionActivity.checkEnableSaveButton();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Type of dish
        ArrayAdapter<String> typeOfDishSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
            android.R.layout.simple_spinner_item, typesOfDish);
        typeOfDishSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeOfDish.setAdapter(typeOfDishSpinnerArrayAdapter);

        mTypeOfDish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mEditionActivity.getRecipe().setTypeOfDish(
                    UserFriendlyTranslationsHandler.getTypeOfDishCodeByPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // Difficulty
        ArrayAdapter<String> difficultySpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
            android.R.layout.simple_spinner_item, difficulties);
        difficultySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDifficulty.setAdapter(difficultySpinnerArrayAdapter);

        mDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mEditionActivity.getRecipe().setDifficulty(
                    UserFriendlyTranslationsHandler.getDifficultyCodeByPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Cooking time
        SeekBar.OnSeekBarChangeListener cookingTimeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCookingTimeLabel.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
                    mCookingTimeHours.getProgress(),
                    mCookingTimeMinutes.getProgress(), mEditionActivity));

                mEditionActivity.getRecipe().setCookingTime(
                    mCookingTimeHours.getProgress() * 60.0F +
                        mCookingTimeMinutes.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        mCookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);
        mCookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);

        // Servings
        // On saving recipe: servings = mServings.getProgress + 1
        mServings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mServingsLabel.setText(UserFriendlyTranslationsHandler.getServingsLabel(
                    mServings.getProgress() + 1, getActivity()));

                mEditionActivity.getRecipe().setServings(mServings.getProgress() + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        mSource.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mEditionActivity.getRecipe().setSource(mSource.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        onReloadView();

        return mLayout;
    }

    /**
     * Reload all UI elements with the recipe data.
     */
    public void onReloadView() {

        // Set initial data
        mTitle.setText(mEditionActivity.getRecipe().getTitle());
        mImage.setText(mEditionActivity.getRecipe().getImage());
        mTypeOfDish.setSelection(UserFriendlyTranslationsHandler.getTypeOfDishPosition(
            mEditionActivity.getRecipe().getTypeOfDish()));
        mDifficulty.setSelection(UserFriendlyTranslationsHandler.getDifficultyPosition(
            mEditionActivity.getRecipe().getDifficulty()));
        mCookingTimeHours.setProgress(mEditionActivity.getRecipe().getCookingTime().intValue() / 60);
        mCookingTimeMinutes.setProgress(mEditionActivity.getRecipe().getCookingTime().intValue() % 60);
        mServings.setProgress((mEditionActivity.getRecipe().getServings() > 0) ?
            mEditionActivity.getRecipe().getServings() - 1 : 0);
        mSource.setText(mEditionActivity.getRecipe().getSource());

        // Check initial data
        mServingsLabel.setText(UserFriendlyTranslationsHandler.getServingsLabel(
            mServings.getProgress() + 1, getActivity()));

        mCookingTimeLabel.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
            mCookingTimeHours.getProgress(), mCookingTimeMinutes.getProgress(),
            mEditionActivity));

        mEditionActivity.checkRequiredValidation(mTitle);
        mEditionActivity.checkURLValidation(mImage);

        mEditionActivity.checkEnableSaveButton();

        // Set categories
        mCategories.removeAllViews();

        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);

            View v = mInflater.inflate(R.layout.item_edition_category, mCategories, false);

            TextView categoryTextView = (TextView) v.findViewById(R.id.category);
            categoryTextView.setText(category);

            CheckBox categoryCheck = (CheckBox) v.findViewById(R.id.checkbox);
            categoryCheck.setTag(UserFriendlyTranslationsHandler.getCategoryCodeByPosition(i));

            if (mEditionActivity.getForcedCategories().contains(
                UserFriendlyTranslationsHandler.getCategoryCodeByPosition(i))) {
                // Category forced by ingredients check
                categoryCheck.setChecked(true);
                categoryCheck.setEnabled(false);
            } else {
                // Category available
                boolean found = false;
                for (RecipeCategory c : mEditionActivity.getRecipe().getCategories()) {
                    if (c.getName().equals(UserFriendlyTranslationsHandler.getCategoryCodeByPosition(i))) {
                        found = true;
                        break;
                    }
                }

                categoryCheck.setChecked(found);
                categoryCheck.setEnabled(true);

                categoryCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        String categoryCode = (String) compoundButton.getTag();

                        if (b) {
                            // Add category to recipe
                            mEditionActivity.getRecipe().getCategories().add(new RecipeCategory(categoryCode));
                        } else {
                            // Remove category from recipe
                            int index = -1;

                            for (int c = 0; c < mEditionActivity.getRecipe().getCategories().size(); c++) {
                                if (mEditionActivity.getRecipe().getCategories().get(c).getName().equals(categoryCode)) {
                                    index = c;
                                    break;
                                }
                            }

                            if (index > -1) {
                                mEditionActivity.getRecipe().getCategories().remove(index);
                            }
                        }
                    }
                });
            }

            mCategories.addView(v);
        }
    }
}
