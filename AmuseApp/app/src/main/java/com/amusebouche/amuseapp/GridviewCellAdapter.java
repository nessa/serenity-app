package com.amusebouche.amuseapp;

import android.app.Fragment;
import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
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

    public GridviewCellAdapter(Context c, int screenWidth) {
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

    // Create a new view for each item referenced by the adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            layout = mInflater.inflate(R.layout.cell_gridview, null);
            layout.setLayoutParams(new GridView.LayoutParams(mScreenWidth / 2 - 2,
                    mScreenWidth / 2 - 2));

            // Get the item in the adapter
            //MyObject myObject = getItem(position);

            // Get the textview to update the string
            TextView name = (TextView) layout.findViewById(R.id.recipe_name);
            name.setText(mStringIds[position]);

            // Get the image to update the content
            ImageView image = (ImageView) layout.findViewById(R.id.recipe_image);
            image.setImageResource(mThumbIds[position]);

            // Save present string into the image button
            // TODO: Save present recipe ID
            ImageButton imageButton = (ImageButton) layout.findViewById((R.id.fav_button));
            imageButton.setTag(mStringIds[position]);

            // TODO: Check if present recipe if favorited, and change imagebutton icon


            // Calling transition from image (to detail image)
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

            // TODO: Calling fav method
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