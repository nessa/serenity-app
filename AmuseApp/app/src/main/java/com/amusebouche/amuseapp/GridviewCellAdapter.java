package com.amusebouche.amuseapp;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
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

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private LayoutInflater mInflater;
    private int mScreenWidth;
    private JSONArray mRecipes;

    public GridviewCellAdapter(Context c, int screenWidth) {
        mContext = c;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecipes = new JSONArray();
    }

    public GridviewCellAdapter(Context c, int screenWidth, JSONArray recipes) {
        mContext = c;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecipes = recipes;
    }

    public void setRecipes(JSONArray recipes) {
        mRecipes = recipes;
    }

    public int getCount() {
        return mRecipes.length();
    }

    public JSONObject getItem(int position) {
        try {
            return mRecipes.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();

            // Insert error data
            return new JSONObject();
        }
    }

    public long getItemId(int position) {
        return 0;
    }

    // Create a new view for each item referenced by the adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            layout = mInflater.inflate(R.layout.cell_gridview, null);
            layout.setLayoutParams(new GridView.LayoutParams(mScreenWidth / 2 - 2,
                    mScreenWidth / 2 - 2));

            // Prepare elements
            TextView name = (TextView) layout.findViewById(R.id.recipe_name);
            ImageView image = (ImageView) layout.findViewById(R.id.recipe_image);
            ImageButton imageButton = (ImageButton) layout.findViewById((R.id.fav_button));

            try {
                // Get the item in the adapter
                JSONObject presentRecipe = getItem(position);

                Log.d("INFO", presentRecipe.getString("title"));

                // Get the textview to update the string
                name.setText(presentRecipe.getString("title"));

                // Get the image to update the content
                new DownloadImageTask(image).execute(presentRecipe.getString("image"));

                // Save present recipe ID into the button
                imageButton.setTag(presentRecipe.getInt("id"));

                // TODO: Check if present recipe if favorited, and change imagebutton icon

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Call transition from image (to detail image)
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

                    // Get main activity from context
                    MainActivity x = (MainActivity) mContext;
                    x.getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment2)
                            .addSharedElement(v, "SharedImage")
                            .addToBackStack("detailBack")
                            .commit();
            }});

            // TODO: Call fav method
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast
                    Toast toast = Toast.makeText(mContext, "Recipe " + v.getTag(),
                        Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                    toast.show();
            }});

        }

        return layout;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}