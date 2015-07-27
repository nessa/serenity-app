package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import com.amusebouche.data.Recipe;
import com.amusebouche.ui.ImageManager;
import com.squareup.picasso.Picasso;


/**
 * Gridview cell adapter class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * It declares the view of each gridview cells that contains:
 * - Recipe image.
 * - Recipe name.
 * - Fav button.
 *
 * Related layouts:
 * - Content: cell_gridview.xml
 */
public class GridviewCellAdapter extends BaseAdapter {
    private Context mContext;
    private Fragment mFragment;
    private LayoutInflater mInflater;
    private int mScreenWidth;
    private ArrayList<Recipe> mRecipes;

    public GridviewCellAdapter(Context c, Fragment f, int screenWidth) {
        mContext = c;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecipes = new ArrayList<>();
    }

    public GridviewCellAdapter(Context c, Fragment f, int screenWidth, ArrayList<Recipe> recipes) {
        mContext = c;
        mFragment = f;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecipes = recipes;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        mRecipes = recipes;
    }

    public int getCount() {
        return mRecipes.size();
    }

    public Recipe getItem(int position) {
        return mRecipes.get(position);
    }

    public long getItemId(int position) {
        return Long.valueOf(mRecipes.get(position).getId());
    }

    // Create a new view for each item referenced by the adapter

    /**
     * Create a new view for each item referenced by the adapter. If the view already existed, it
     * will update it.
     * @param position Position of this view in the gridview.
     * @param convertView Existing view (it may not exist, so it will be null).
     * @param parent
     * @return New view.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell;
        TextView name;
        final ImageView image;
        ImageButton imageButton;

        // If the view didn't exist, we create a new one
        if (convertView == null) {
            cell = mInflater.inflate(R.layout.cell_gridview, null);
            cell.setLayoutParams(new GridView.LayoutParams(mScreenWidth / 2 - 2,
                    mScreenWidth / 2 - 2));
        } else {
            cell = (View)convertView;
        }

        // Get the item in the adapter
        final Recipe presentRecipe = getItem(position);

        // Get the textview to update the string
        name = (TextView) cell.findViewById(R.id.recipe_name);
        name.setText(presentRecipe.getTitle());

        // Get the image to update the content
        image = (ImageView) cell.findViewById(R.id.recipe_image);
        image.setTag(position);
        ImageManager.setCellImage(mContext, presentRecipe.getImage(), image);

        // TODO: Set this!
        // Save present recipe ID into the button
        /*
        imageButton = (ImageButton) cell.findViewById((R.id.fav_button));
        imageButton.setTag(this.getItemId(position));*/


        // TODO: Check if present recipe if favorited, and change imagebutton icon


        // Call transition from image (to detail image)
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new ChangeImageTransform());
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.setDuration(300);

                Fragment fragment2 = new RecipeDetailFragment();
                fragment2.setSharedElementEnterTransition(transitionSet);
                fragment2.setSharedElementReturnTransition(transitionSet);
                Fade fade = new Fade();
                fade.setStartDelay(300);
                fragment2.setEnterTransition(fade);

                Log.d("INFO", "Vista " + v.getTag());
                mRecipes.get((int) v.getTag()).printString();

                final Bundle bundle = new Bundle();
                bundle.putParcelable("recipe", mRecipes.get((int) v.getTag()));
                fragment2.setArguments(bundle);

                // Get main activity from context
                MainActivity x = (MainActivity) mContext;
                x.getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment2)
                        .addSharedElement(v, "SharedImage")
                        .addToBackStack("detailBack")
                        .commit();

                // intent.putExtra("recipe", new Recipe(...));
                */

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d("INFO", "NEWER DEVICE");

                    MainActivity x = (MainActivity) mContext;


                    mFragment.setSharedElementReturnTransition(TransitionInflater.from(mContext)
                            .inflateTransition(R.transition.shared_image_transition));
                    mFragment.setExitTransition(TransitionInflater.from(mContext)
                            .inflateTransition(android.R.transition.explode));

                    /*
                    setSharedElementReturnTransition(TransitionInflater.from(mContext)
                            .inflateTransition(R.transition.shared_image_transition));
                    setExitTransition(TransitionInflater.from(mContext)
                            .inflateTransition(android.R.transition.explode));
                            */

                    // Create new fragment to add (Fragment B)
                    Fragment fragment = new RecipeDetailFragment();
                    fragment.setSharedElementEnterTransition(TransitionInflater.from(mContext).inflateTransition(R.transition.shared_image_transition));
                    fragment.setEnterTransition(TransitionInflater.from(mContext).inflateTransition(android.R.transition.explode));

                    // Our shared element (in Fragment A) = image
                    //mProductImage   = (ImageView) mLayout.findViewById(R.id.product_detail_image);


                    final Bundle bundle = new Bundle();
                    bundle.putParcelable("recipe", mRecipes.get((int) v.getTag()));
                    fragment.setArguments(bundle);

                    // Add Fragment B
                    x.getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack("detailBack")
                            .addSharedElement(image, "SharedImage")
                            .commit();
                } else {
                    // Code to run on older devices
                    Log.d("INFO", "OLDER DEVICE");
                }
            }});

        // TODO: Call fav method
        /*
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast
                Toast toast = Toast.makeText(mContext, "Recipe " + v.getTag(),
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                toast.show();
            }});*/

        return cell;
    }

}