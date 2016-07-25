package com.amusebouche.services;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amusebouche.activities.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * Image handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android support class.
 * It contains useful methods to handle images.
 */
public class ImageHandler {

    private static int RESIZE_PIXELS = 500;

    /**
     * Set an image in a image view using the Picasso library.
     * @param context Context where the image will be set.
     * @param imageName Name of the image. Could be an URL, a file path or an empty string. If it's
     *                  an empty string, we will set a random sample image.
     * @param imageView Image view we want to set up.
     * @see Picasso library: com.squareup.picasso.Picasso
     */
    public static void setCellImage(Context context, String imageName, ImageView imageView) {
        ImageHandler.setCellImage(context, imageName, imageView, null);
    }

    /**
     * Set an image in a image view using the Picasso library.
     * @param context Context where the image will be set.
     * @param imageName Name of the image. Could be an URL, a file path or an empty string. If it's
     *                  an empty string, we will set a random sample image.
     * @param imageView Image view we want to set up.
     * @see Picasso library: com.squareup.picasso.Picasso
     */
    public static void setCellImage(Context context, String imageName, ImageView imageView,
                                    final ProgressBar progressBar) {
        // Get a random default image
        Random r = new Random();
        int randomNumber = (r.nextInt(3));
        int resource;

        switch (randomNumber) {
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

        // Define callback for progress bar if it's defined
        Callback callback = null;
        if (progressBar != null) {
            callback = new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                }
            };
        }

        // Check if image is defined correctly. If not, load the alternative resource.
        if (imageName == null || imageName.equals("")) {
            Picasso.with(context)
                    .load(resource)
                    .resize(RESIZE_PIXELS, RESIZE_PIXELS)
                    .centerInside()
                    .noFade()
                    .into(imageView, callback);
        } else {
            Picasso.with(context)
                    .load(imageName)
                    .resize(RESIZE_PIXELS, RESIZE_PIXELS)
                    .centerInside()
                    .noFade()
                    .error(resource)
                    .into(imageView, callback);
        }
    }

}
