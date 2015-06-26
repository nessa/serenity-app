package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amusebouche.data.Recipe;
import com.squareup.picasso.Picasso;

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
    private LinearLayout mLayout;
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

        mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_recipe_detail,
                container, false);

        ImageView image = (ImageView) mLayout.findViewById(R.id.recipe_image);
        this.setCellImage(mRecipe.getImage(), image);

        TextView textView = (TextView) mLayout.findViewById(R.id.recipe_name);
        textView.setText(mRecipe.getTitle());


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


    /**
     * Set an image in a image view using the Picasso library.
     * @param imageName Name of the image. Could be an URL, a file path or an empty string. If it's
     *                  an empty string, we will set a random sample image.
     * @param imageView Image view we want to set up.
     * @see Picasso library: com.squareup.picasso.Picasso
     */
    private void setCellImage(String imageName, ImageView imageView) {
        if (imageName == "") {
            // Get a random default image
            // TODO: Update these images with new ones
            Random r = new Random();
            int randomNumber = (r.nextInt(8));
            int resource;

            switch (randomNumber) {
                default:
                case 0:
                    resource = R.drawable.sample_0;
                    break;
                case 1:
                    resource = R.drawable.sample_1;
                    break;
                case 2:
                    resource = R.drawable.sample_2;
                    break;
                case 3:
                    resource = R.drawable.sample_3;
                    break;
                case 4:
                    resource = R.drawable.sample_4;
                    break;
                case 5:
                    resource = R.drawable.sample_5;
                    break;
                case 6:
                    resource = R.drawable.sample_6;
                    break;
                case 7:
                    resource = R.drawable.sample_7;
                    break;

            }

            Picasso.with(getActivity().getApplicationContext()).load(resource).into(imageView);
        } else {
            Picasso.with(getActivity().getApplicationContext()).load(imageName).into(imageView);
        }
    }
}
