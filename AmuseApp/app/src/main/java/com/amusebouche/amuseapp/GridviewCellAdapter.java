package com.amusebouche.amuseapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.ArrayList;

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
    private Fragment mFragment;
    private LayoutInflater mInflater;
    private int mScreenWidth;
    private ArrayList<Recipe> mRecipes;

    private String filename = "presentRecipeImage.png";

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
        ProgressBar progressBar;
        final ImageView image;
        ImageButton imageButton;

        // If the view didn't exist, we create a new one
        if (convertView == null) {
            cell = mInflater.inflate(R.layout.cell_gridview, null);
            cell.setLayoutParams(new GridView.LayoutParams(mScreenWidth / 2 - 2,
                    mScreenWidth / 2 - 2));
        } else {
            cell = convertView;
        }

        // Get the item in the adapter
        final Recipe presentRecipe = getItem(position);

        // Get the textview to update the string
        name = (TextView) cell.findViewById(R.id.recipe_name);
        name.setText(presentRecipe.getTitle());

        progressBar = (ProgressBar) cell.findViewById(R.id.progressBar);
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


        // TODO: Check if present recipe if favorited, and change imagebutton icon


        // Call transition from image (to detail image)
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    // Send selected recipe to the next activity
                    Intent i = new Intent(mContext, DetailActivity.class);
                    i.putExtra("recipe", mRecipes.get((int) v.getTag()));

                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((MainActivity) mContext, image, "SharedImage");
                    ActivityCompat.startActivity((MainActivity) mContext, i, options.toBundle());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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