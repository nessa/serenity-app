package com.amusebouche.amuseapp;


import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amusebouche.ui.ImageManager;


/**
 * Detail activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Detail app activity (recipe detail).
 * It has:
 * - The present fragment that is active.
 *
 * Related layouts:
 * - Content: activity_detail.xml
 */
public class MediaActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String mediaType = i.getStringExtra("mediaType");
        String elementUri = i.getStringExtra("elementUri");
        String directionNumber = i.getStringExtra("directionNumber");

        setContentView(R.layout.activity_media);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.detail_direction_label) + " " + directionNumber);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);

        if (mediaType.equals("IMAGE")) {
            ImageView image = new ImageView(this);
            image.setBackgroundColor(getResources().getColor(R.color.dark_background));
            image.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            ImageManager.setCellImage(this, elementUri, image);

            container.addView(image);
        } else {
            if (mediaType.equals("VIDEO")) {
                Log.d("INFO", "VIDEO");
                // TODO
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(getClass().getSimpleName(), "onBackPressed()");
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("DETAIL", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("DETAIL", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

}