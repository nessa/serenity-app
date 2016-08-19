package com.amusebouche.services;

import android.content.Context;
import android.widget.ImageView;

import com.amusebouche.activities.R;
import com.bumptech.glide.Glide;

/**
 * Image handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android support class.
 * It contains useful methods to handle images.
 */
public class ImageHandler {

    /**
     * Set an image in a image view using the Picasso library.
     * @param context Context where the image will be set.
     * @param imageName Name of the image. Could be an URL, a file path or an empty string. If it's
     *                  an empty string, we will set a random sample image.
     * @param imageView Image view we want to set up.
     * @param position Integer position of the image in the list of elements
     * @see Glide library: com.github.bumptech.glide
     */
    public static void setCellImage(Context context, String imageName, ImageView imageView,
                                    int position) {
        // Get a default image from the image position
        int resource;
        int resourceId = position % 3;
        switch (resourceId) {
            default:
            case 0:
                resource = R.drawable.fake_food_1;
                break;
            case 1:
                resource = R.drawable.fake_food_2;
                break;
            case 2:
                resource = R.drawable.fake_food_3;
                break;
        }

        // Check if image is defined correctly. If not, load the alternative resource.
        if (imageName == null || imageName.equals("")) {
            imageView.setImageDrawable(context.getDrawable(resource));
        } else {
            Glide.with(context)
                    .load(imageName)
                    .centerCrop()
                    .error(resource)
                    .into(imageView);
        }
    }

    /**
     * Set an image in a image view using the Picasso library.
     * @param context Context where the image will be set.
     * @param imageName Name of the image. Could be an URL, a file path or an empty string. If it's
     *                  an empty string, we will set a random sample image.
     * @param imageView Image view we want to set up.
     * @see Glide library: com.github.bumptech.glide
     */
    public static void setImage(Context context, String imageName, ImageView imageView) {
        Glide.with(context)
            .load(imageName)
            .into(imageView);
    }
}
