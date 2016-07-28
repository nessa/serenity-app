package com.amusebouche.services;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amusebouche.activities.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Image handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android support class.
 * It contains useful methods to handle images.
 */
public class ImageHandler {

    private static final AtomicInteger RESIZE_PIXELS = new AtomicInteger(500);

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
        // Get a default image from the image position
        int resource;
        int position = (int)imageView.getTag() % 3;
        switch (position) {
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
            // Define callback for progress bar and enable it if it's defined
            Callback callback = null;
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);

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

            Picasso.with(context)
                    .load(imageName)
                    .resize(RESIZE_PIXELS.get(), RESIZE_PIXELS.get())
                    .centerInside()
                    .noFade()
                    .error(resource)
                    .into(imageView, callback);
        }
    }

}
