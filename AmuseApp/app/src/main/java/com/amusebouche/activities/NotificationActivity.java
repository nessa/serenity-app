package com.amusebouche.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Notification activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com>
 *
 * Needed to load the app exactly as it was before from a notification.
 */
public class NotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isTaskRoot()) {
            // Start the app before finishing
            Intent startAppIntent = new Intent(getApplicationContext(), MainActivity.class);
            startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startAppIntent);
        }

        /* Now finish, which will drop the user into the activity that was at the top
         * of the task stack or into the main activity */
        finish();
    }
}
