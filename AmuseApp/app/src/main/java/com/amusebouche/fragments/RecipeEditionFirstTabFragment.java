package com.amusebouche.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    // UI
    private SimpleRecyclerAdapter mAdapter;

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
     * Simple adapter for a recycler view
     */
    public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

        private EditionActivity mContext;
        private LayoutInflater mInflater;

        public SimpleRecyclerAdapter(Context context, LayoutInflater inflater) {
            this.mContext = (EditionActivity) context;
            this.mInflater = inflater;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private EditText title;
            private EditText image;
            private TextView cookingTimeLabel;
            private SeekBar cookingTimeHours;
            private SeekBar cookingTimeMinutes;
            private TextView servingsLabel;
            private Spinner typeOfDish;
            private Spinner difficulty;
            private SeekBar servings;
            private EditText source;
            private LinearLayout categories;


            public ViewHolder(final View view) {
                super(view);

                // Set UI elements
                title = (EditText) view.findViewById(R.id.title);
                image = (EditText) view.findViewById(R.id.image);
                typeOfDish = (Spinner) view.findViewById(R.id.type_of_dish);
                difficulty = (Spinner) view.findViewById(R.id.difficulty);
                cookingTimeLabel = (TextView) view.findViewById(R.id.cooking_time);
                cookingTimeHours = (SeekBar) view.findViewById(R.id.cooking_time_hours);
                cookingTimeMinutes = (SeekBar) view.findViewById(R.id.cooking_time_minutes);
                servingsLabel = (TextView) view.findViewById(R.id.servings_label);
                servings = (SeekBar) view.findViewById(R.id.servings);
                source = (EditText) view.findViewById(R.id.source);
                categories = (LinearLayout) view.findViewById(R.id.categories);
            }
        }

        @Override
        public SimpleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_edition_first_tab_content, parent, false);

            // create ViewHolder
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // Title
            holder.title.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    mContext.getRecipe().setTitle(holder.title.getText().toString());

                    mContext.checkRequiredValidation(holder.title);

                    mContext.checkEnableSaveButton();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            // Image
            holder.image.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    mContext.getRecipe().setImage(holder.image.getText().toString());

                    mContext.checkURLValidation(holder.image);

                    mContext.checkEnableSaveButton();
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
            holder.typeOfDish.setAdapter(typeOfDishSpinnerArrayAdapter);

            holder.typeOfDish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    mContext.getRecipe().setTypeOfDish(
                            UserFriendlyTranslationsHandler.getTypeOfDishCodeByPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}
            });

            // Difficulty
            ArrayAdapter<String> difficultySpinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, difficulties);
            difficultySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.difficulty.setAdapter(difficultySpinnerArrayAdapter);

            holder.difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    mContext.getRecipe().setDifficulty(
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
                    holder.cookingTimeLabel.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
                            holder.cookingTimeHours.getProgress(),
                            holder.cookingTimeMinutes.getProgress(), getActivity()));

                    mContext.getRecipe().setCookingTime(
                            holder.cookingTimeHours.getProgress() * 60.0F +
                            holder.cookingTimeMinutes.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };

            holder.cookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);
            holder.cookingTimeMinutes.setOnSeekBarChangeListener(cookingTimeListener);

            // Servings
            // On saving recipe: servings = mServings.getProgress + 1
            holder.servings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    holder.servingsLabel.setText(UserFriendlyTranslationsHandler.getServingsLabel(
                            holder.servings.getProgress() + 1, getActivity()));

                    mContext.getRecipe().setServings(holder.servings.getProgress() + 1);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });


            holder.source.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    mContext.getRecipe().setSource(holder.source.getText().toString());
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            // Set initial data
            holder.title.setText(mContext.getRecipe().getTitle());
            holder.image.setText(mContext.getRecipe().getImage());
            holder.typeOfDish.setSelection(UserFriendlyTranslationsHandler.getTypeOfDishPosition(
                    mContext.getRecipe().getTypeOfDish()));
            holder.difficulty.setSelection(UserFriendlyTranslationsHandler.getDifficultyPosition(
                    mContext.getRecipe().getDifficulty()));
            holder.cookingTimeHours.setProgress(mContext.getRecipe().getCookingTime().intValue() / 60);
            holder.cookingTimeMinutes.setProgress(mContext.getRecipe().getCookingTime().intValue() % 60);
            holder.servings.setProgress((mContext.getRecipe().getServings() > 0) ?
                    mContext.getRecipe().getServings() - 1 : 0);
            holder.source.setText(mContext.getRecipe().getSource());

            // Check initial data
            holder.servingsLabel.setText(UserFriendlyTranslationsHandler.getServingsLabel(
                    holder.servings.getProgress() + 1, getActivity()));

            holder.cookingTimeLabel.setText(UserFriendlyTranslationsHandler.getCookingTimeLabel(
                    holder.cookingTimeHours.getProgress(), holder.cookingTimeMinutes.getProgress(),
                    mContext));

            mContext.checkRequiredValidation(holder.title);
            mContext.checkURLValidation(holder.image);

            mContext.checkEnableSaveButton();

            // Set categories
            holder.categories.removeAllViews();

            for (int i = 0; i < categories.size(); i++) {
                String category = categories.get(i);

                View v = mInflater.inflate(R.layout.item_edition_category, holder.categories, false);

                TextView categoryTextView = (TextView) v.findViewById(R.id.category);
                categoryTextView.setText(category);

                CheckBox categoryCheck = (CheckBox) v.findViewById(R.id.checkbox);
                categoryCheck.setTag(UserFriendlyTranslationsHandler.getCategoryCodeByPosition(i));

                if (mContext.getForcedCategories().contains(
                        UserFriendlyTranslationsHandler.getCategoryCodeByPosition(i))) {
                    // Category forced by ingredients check
                    categoryCheck.setChecked(true);
                    categoryCheck.setEnabled(false);
                } else {
                    // Category available
                    boolean found = false;
                    for (RecipeCategory c : mContext.getRecipe().getCategories()) {
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
                                mContext.getRecipe().getCategories().add(new RecipeCategory(categoryCode));
                            } else {
                                // Remove category from recipe
                                int index = -1;

                                for (int c = 0; c < mContext.getRecipe().getCategories().size(); c++) {
                                    if (mContext.getRecipe().getCategories().get(c).getName().equals(categoryCode)) {
                                        index = c;
                                        break;
                                    }
                                }

                                if (index > -1) {
                                    mContext.getRecipe().getCategories().remove(index);
                                }
                            }
                        }
                    });
                }

                holder.categories.addView(v);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}
