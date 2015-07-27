package com.amusebouche.ui;

import android.content.Context;
import android.widget.ImageView;

import com.amusebouche.amuseapp.R;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * Created by nessa on 23/07/15.
 */
public class ImageManager {

    /**
     * Set an image in a image view using the Picasso library.
     * @param context Context where the image will be set.
     * @param imageName Name of the image. Could be an URL, a file path or an empty string. If it's
     *                  an empty string, we will set a random sample image.
     * @param imageView Image view we want to set up.
     * @see Picasso library: com.squareup.picasso.Picasso
     */
    public static void setCellImage(Context context, String imageName, ImageView imageView) {
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

        //Picasso.with(mContext).load(imageName).into(imageView);
        Picasso.with(context).load(imageName)
                .error(resource)
                .placeholder(R.drawable.loading_animation)
                .into(imageView);
    }
}
