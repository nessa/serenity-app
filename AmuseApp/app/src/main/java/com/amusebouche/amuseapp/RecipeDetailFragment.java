package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.amusebouche.data.RecipeDirection;
import com.amusebouche.data.RecipeIngredient;
import com.amusebouche.ui.ImageManager;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.Random;

/**
 * Recipe detail fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains and shows the recipe detailed information.
 *
 * Related layouts:
 * - Menu: menu_recipe_detail.xml
 * - Content: fragment_recipe_detail.xml
 */
public class RecipeDetailFragment extends Fragment {
    private ScrollView mLayout;
    private Recipe mRecipe;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Log.d("INFO", "Set recipe");
            mRecipe = getArguments().getParcelable("recipe");
        } else {
            if (savedInstanceState != null && savedInstanceState.containsKey("recipe")) {
                mRecipe = savedInstanceState.getParcelable("recipe");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
        changeActionButton();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");

        Log.d("INFO", "Set view");

        mLayout = (ScrollView) inflater.inflate(R.layout.fragment_recipe_detail,
                container, false);

        ImageView image = (ImageView) mLayout.findViewById(R.id.recipe_image);
        ImageManager.setCellImage(getActivity().getApplicationContext(), mRecipe.getImage(), image);

        TextView nameTextView = (TextView) mLayout.findViewById(R.id.recipe_name);
        nameTextView.setText(mRecipe.getTitle());

        TextView ownerTextView = (TextView) mLayout.findViewById(R.id.owner_name);
        ownerTextView.setText(mRecipe.getOwner());

        TextView typeOfDishTextView = (TextView) mLayout.findViewById(R.id.type_of_dish);
        typeOfDishTextView.setText(this.getTypeOfDish(mRecipe.getTypeOfDish()));

        TextView difficultyTextView = (TextView) mLayout.findViewById(R.id.difficulty);
        difficultyTextView.setText(this.getDifficulty(mRecipe.getDifficulty()));

        TextView cookingTimeTextView = (TextView) mLayout.findViewById(R.id.cooking_time);
        cookingTimeTextView.setText(Objects.toString(mRecipe.getCookingTime()));

        TextView servingsTextView = (TextView) mLayout.findViewById(R.id.servings);
        servingsTextView.setText(Objects.toString(mRecipe.getServings()));

        TextView sourceTextView = (TextView) mLayout.findViewById(R.id.source);
        sourceTextView.setText(mRecipe.getSource());

        if (mRecipe.getSource().startsWith("http://") || mRecipe.getSource().startsWith("https://")) {
            sourceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchWebIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mRecipe.getSource()));
                    startActivity(launchWebIntent);
                }
            });
        }

        float rating = 0;
        if (mRecipe.getUsersRating() != 0) {
            rating = (float)mRecipe.getTotalRating()/ (float)mRecipe.getUsersRating();
        }

        TextView ratingTextView = (TextView) mLayout.findViewById(R.id.rating);
        ratingTextView.setText(Objects.toString(rating));

        // Ingredients
        LinearLayout ingredientsLayout = (LinearLayout) mLayout.findViewById(R.id.ingredients);

        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            RecipeIngredient presentIngredient = (RecipeIngredient)mRecipe.getIngredients().get(i);

            LinearLayout ingredientLayout = (LinearLayout) inflater.inflate(
                    R.layout.fragment_recipe_detail_ingredient, null);

            TextView quantity = (TextView) ingredientLayout.findViewById(R.id.quantity);
            quantity.setText(Objects.toString(presentIngredient.getQuantity()));

            TextView measurement_unit = (TextView) ingredientLayout.findViewById(R.id.measurement_unit);
            measurement_unit.setText(presentIngredient.getMeasurementUnit());

            TextView name = (TextView) ingredientLayout.findViewById(R.id.name);
            name.setText(presentIngredient.getName());

            ingredientsLayout.addView(ingredientLayout);
        }

        // Directions
        LinearLayout directionsLayout = (LinearLayout) mLayout.findViewById(R.id.directions);

        for (int d = 0; d < mRecipe.getDirections().size(); d++) {
            Log.d("INFO", "direction");
            RecipeDirection presentDirection = (RecipeDirection)mRecipe.getDirections().get(d);

            LinearLayout directionLayout = (LinearLayout) inflater.inflate(
                    R.layout.fragment_recipe_detail_direction, null);

            TextView number = (TextView) directionLayout.findViewById(R.id.number);
            number.setText(Objects.toString(presentDirection.getSortNumber()));

            TextView description = (TextView) directionLayout.findViewById(R.id.description);
            description.setText(presentDirection.getDescription());

            LinearLayout extraLayout = (LinearLayout) directionLayout.findViewById(R.id.extra);

            if (!presentDirection.getImage().equals("")) {
                ImageView directionImage = new ImageView(extraLayout.getContext());
                ImageManager.setCellImage(getActivity().getApplicationContext(),
                        presentDirection.getImage(), directionImage);
                extraLayout.addView(directionImage);
            }

            if (presentDirection.getTime() > 0) {
                Button cronoButton = new Button(extraLayout.getContext());
                cronoButton.setText(R.string.chronometer);
                extraLayout.addView(cronoButton);
            }

            directionsLayout.addView(directionLayout);
        }

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(getClass().getSimpleName(), "onHidden()");
        if (!hidden) {
            Log.i(getClass().getSimpleName(), "Not hidden");
            changeActionButton();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_fav:
                makeFavorite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void makeFavorite() {
        Log.v("INFO", "Favorite X");
        // TODO: Implement this method.
    }

    /* Instead of using the action bar method setNavigationMode, we define specifically the
     * buttons to show. We call this method when the app is created the first time (onResume) and
     * every time it appears again (onHiddenChange). */
    private void changeActionButton() {
        MainActivity x = (MainActivity) getActivity();
        x.setDrawerIndicatorEnabled(false);
    }

    private String getTypeOfDish(String code) {
        switch(code) {
            case "APPETIZER":
                return getString(R.string.type_of_dish_appetizer);
            case "FIRST-COURSE":
                return getString(R.string.type_of_dish_first_course);
            case "SECOND-COURSE":
                return getString(R.string.type_of_dish_second_course);
            case "MAIN-DISH":
                return getString(R.string.type_of_dish_main_dish);
            case "DESSERT":
                return getString(R.string.type_of_dish_dessert);
            default:
            case "OTHER":
                return getString(R.string.type_of_dish_other);
        }
    }

    private String getDifficulty(String code) {
        switch(code) {
            case "HIGH":
                return getString(R.string.difficulty_high);
            case "LOW":
                return getString(R.string.difficulty_low);
            default:
            case "MEDIUM":
                return getString(R.string.difficulty_medium);
        }
    }
}
