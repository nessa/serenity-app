package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amusebouche.activities.MainActivity;
import com.amusebouche.activities.R;

/**
 * Information fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains app information.
 *
 * Related layouts:
 * - Content: fragment_information.xml
 */
public class InformationFragment extends Fragment {

    // Data variables
    private MainActivity mMainActivity;

    // LIFECYCLE METHODS

    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragemnt activity (DetailActivity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view
     * hierarchy instantiated.
     * @param savedInstanceState State of the fragment if it's being re-created.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Called to do initial creation of a fragment. This is called after onAttach and before
     * onCreateView.
     * @param savedInstanceState Saved state (if the fragment is being re-created)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate()");

        mMainActivity = (MainActivity) getActivity();
    }


    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * onCreate and onActivityCreated, onViewStateRestored, onStart().
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment,
     * @param container  This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If this fragment is being re-constructed from a previous saved
     *                           state as given here.
     * @return Return the View for the this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreateView()");

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_information,
                container, false);

        populateViewForOrientation(inflater, layout);

        return layout;
    }

    /**
     * Called when the configuration changes (like orientation).
     * @param newConfig The new configuration.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LayoutInflater inflater = LayoutInflater.from(mMainActivity);
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    /**
     * Redefine all user interface elements. Needed in case orientation changes.
     * @param inflater Layout constructor
     * @param viewGroup Last view
     */
    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View subview = inflater.inflate(R.layout.fragment_information, viewGroup);

        String version = "";
        try {
            PackageInfo pInfo = mMainActivity.getPackageManager().getPackageInfo(
                    mMainActivity.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = getString(R.string.info_app_name) + " " + version;

        TextView appName = (TextView) subview.findViewById(R.id.app_name);
        appName.setText(version);


        Button licensesButton = (Button) subview.findViewById(R.id.license_button);
        licensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog licensesDialog = new WebViewDialog(getActivity(), "license.html");
                licensesDialog.show();
            }
        });

        Button thirdPartyLicensesButton = (Button) subview.findViewById(R.id.third_party_licenses_button);
        thirdPartyLicensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog licensesDialog = new WebViewDialog(getActivity(), "third_party_licenses.html");
                licensesDialog.show();
            }
        });
    }


    public class WebViewDialog extends Dialog {

        // UI elements
        protected WebView webView;
        protected Button acceptButton;

        /**
         * Dialog constructor
         *
         * @param context Application context
         */
        public WebViewDialog(final Context context, String file) {
            // Set your theme here
            super(context);

            this.getWindow().setWindowAnimations(R.style.UpAndDownDialogAnimation);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setContentView(R.layout.dialog_webview);

            // Get layout elements
            webView = (WebView) findViewById(R.id.web_view);
            acceptButton = (Button) findViewById(R.id.accept_button);

            // Set web view
            webView.loadUrl("file:///android_asset/" + file);

            // Set buttons' listeners
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebViewDialog.this.dismiss();
                }
            });
        }
    }

}
