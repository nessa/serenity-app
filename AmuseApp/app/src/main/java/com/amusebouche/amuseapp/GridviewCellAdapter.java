package com.amusebouche.amuseapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileOutputStream;

import com.amusebouche.data.Recipe;
import com.amusebouche.ui.ImageManager;


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
    private RecipeListFragment mFragment;
    private LayoutInflater mInflater;
    private int mScreenWidth;

    private String filename = "presentRecipeImage.png";

    public GridviewCellAdapter(Context c, RecipeListFragment f, int screenWidth) {
        mContext = c;
        mFragment = f;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (((MainActivity) mContext).getRecipes() != null) {
            return ((MainActivity) mContext).getRecipes().size();
        } else {
            return 0;
        }
    }

    public Recipe getItem(int position) {
        return ((MainActivity) mContext).getRecipes().get(position);
    }

    public long getItemId(int position) {
        return Long.valueOf(((MainActivity) mContext).getRecipes().get(position).getId());
    }

    // Create a new view for each item referenced by the adapter

    /**
     * Create a new view for each item referenced by the adapter. If the view already existed, it
     * will update it.
     * @param position Position of this view in the gridview.
     * @param convertView Existing view (it may not exist, so it will be null).
     * @param parent Parent view
     * @return New view.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell;
        final TextView name;
        ProgressBar progressBar;
        final ImageView image;
        final RelativeLayout fadeViews;

        // If the view didn't exist, we create a new one
        if (convertView == null) {
            cell = mInflater.inflate(R.layout.cell_gridview, null);
            cell.setLayoutParams(new GridView.LayoutParams(mScreenWidth /
                    mContext.getResources().getInteger(R.integer.gridview_columns) -
                    mContext.getResources().getInteger(R.integer.gridview_margin),
                    mScreenWidth / mContext.getResources().getInteger(R.integer.gridview_columns) -
                            mContext.getResources().getInteger(R.integer.gridview_margin)));
        } else {
            cell = convertView;
        }

        // Keep downloading next recipes when we show the last ones
        if (position > ((MainActivity) mContext).getPreviousTotal() && position == getCount() -
                mContext.getResources().getInteger(R.integer.gridview_left_items_to_reload)) {
            ((MainActivity) mContext).setPreviousTotal(position);
            mFragment.downloadNextRecipes();
        }

        // Get the item in the adapter
        final Recipe presentRecipe = getItem(position);

        // Get the textview to update the string
        name = (TextView) cell.findViewById(R.id.recipe_name);
        name.setText(presentRecipe.getTitle());

        fadeViews = (RelativeLayout) cell.findViewById(R.id.fade_views);

        progressBar = (ProgressBar) cell.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Get the image to update the content
        image = (ImageView) cell.findViewById(R.id.recipe_image);
        image.setTag(position);
        ImageManager.setCellImage(mContext, presentRecipe.getImage(), image, progressBar);

        // TODO: Set this!
        // Save present recipe ID into the button
        /*
        imageButton = (ImageButton) cell.findViewById((R.id.fav_button));
        imageButton.setTag(this.getItemId(position));*/

        // Call transition from image (to detail image)
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // Can't compress a recycled bitmap so we copy it
                image.buildDrawingCache(true);
                Bitmap bitmap = image.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
                image.destroyDrawingCache();

                try {
                    // Save bitmap into file to prevent transactiontoolargeexception
                    FileOutputStream stream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    // Cleanup
                    stream.close();
                    bitmap.recycle();


                    // Fade out title and view
                    Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
                    fadeOutAnimation.setDuration(1000);

                    fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // Send selected recipe to the next activity
                            Intent i = new Intent(mContext, DetailActivity.class);
                            i.putExtra("recipe", ((MainActivity) mContext).getRecipes().get((int) v.getTag()));

                            Pair<View, String> p1 = Pair.create((View) image, mContext.getString(R.string.transition_detail_image));
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    (MainActivity) mContext, p1);

                            ActivityCompat.startActivity((MainActivity) mContext, i, options.toBundle());
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    fadeViews.startAnimation(fadeOutAnimation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return cell;
    }

}