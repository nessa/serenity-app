package com.amusebouche.amuseapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private int mScreenWidth;

    public ImageAdapter(Context c, int screenWidth) {
        mContext = c;
        mScreenWidth = screenWidth;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        // TODO: Update this to get the proper item
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            layout = mInflater.inflate(R.layout.gridview_cell, null);
            layout.setLayoutParams(new GridView.LayoutParams(mScreenWidth / 2 - 2,
                    mScreenWidth / 2 - 2));

            /* Get the item in the adapter */
            //MyObject myObject = getItem(position);

            // Get textview to update the string
            TextView name = (TextView) layout.findViewById(R.id.recipe_name);
            name.setText(mStringIds[position]);

            // Get the image to update the content
            ImageView image = (ImageView) layout.findViewById(R.id.recipe_image);
            image.setImageResource(mThumbIds[position]);

            // Save present string in image button
            // TODO: Save present recipe ID
            ImageButton imageButton = (ImageButton) layout.findViewById((R.id.fav_button));
            imageButton.setTag(mStringIds[position]);

            // TODO: Check if present recipe if favorited, and change imagebutton icon

            // Calling transition from image (to detail image)
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get main activity from context
                    MainActivity x = (MainActivity) mContext;
                    Intent intent = new Intent(x, RecipeDetailActivity.class);
                    //String transitionName = getString(R.string.transition_recipe_detail);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            x,
                            v, // The view which starts the transition
                            "detail" // The transitionName of the view we’re transitioning to
                    );
                    x.startActivity(intent, options.toBundle());

            }});

        }

        return layout;
    }

    // test data
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };
    private String[] mStringIds = {
            "Sample 2", "Sample 3",
            "Sample 4", "Sample 5",
            "Sample 6", "Sample 7",
            "Sample 0", "Sample 1",
            "Sample 2", "Sample 3",
            "Sample 4", "Sample 5",
            "Sample 6", "Sample 7",
            "Sample 0", "Sample 1",
            "Sample 2", "Sample 3",
            "Sample 4", "Sample 5",
            "Sample 6", "Sample 7"
    };
}