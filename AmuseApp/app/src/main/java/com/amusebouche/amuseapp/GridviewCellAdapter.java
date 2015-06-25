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
import java.util.ArrayList;
import java.util.Random;

import com.amusebouche.data.Recipe;

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
    private ArrayList<Recipe> mRecipes;

    public GridviewCellAdapter(Context c, int screenWidth) {
        mContext = c;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecipes = new ArrayList<>();
    }

    public GridviewCellAdapter(Context c, int screenWidth, ArrayList<Recipe> recipes) {
        mContext = c;
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
        return 0;
    }

    // Create a new view for each item referenced by the adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            layout = mInflater.inflate(R.layout.cell_gridview, null);
            layout.setLayoutParams(new GridView.LayoutParams(mScreenWidth / 2 - 2,
                    mScreenWidth / 2 - 2));

            // Get the item in the adapter
            Recipe presentRecipe = getItem(position);

            // Get the textview to update the string
            TextView name = (TextView) layout.findViewById(R.id.recipe_name);
            name.setText(presentRecipe.getTitle());

            // Get the image to update the content
            ImageView image = (ImageView) layout.findViewById(R.id.recipe_image);
            new DownloadImageTask(image).execute(presentRecipe.getImage());

            // Save present recipe ID into the button
            ImageButton imageButton = (ImageButton) layout.findViewById((R.id.fav_button));
            imageButton.setTag(presentRecipe.getId());

            // TODO: Check if present recipe if favorited, and change imagebutton icon


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
            String urlDisplay = urls[0];
            Boolean failed = false;
            Bitmap mIcon11 = null;

            if (urlDisplay == "") {
                failed = true;
            } else {
                if (urlDisplay.startsWith("http://") || urlDisplay.startsWith("https://")) {
                    // Try to get image from URL
                    try {
                        InputStream in = new java.net.URL(urlDisplay).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                        e.printStackTrace();
                        failed = true;
                    }
                } else {
                    // TODO: Try to get image from local storage
                    //File image = new File(sd+filePath, imageName);
                    //Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

                    // If it fails:
                    failed = true;
                }
            }

            if (failed) {
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

                mIcon11 = BitmapFactory.decodeResource(mContext.getResources(), resource);
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}