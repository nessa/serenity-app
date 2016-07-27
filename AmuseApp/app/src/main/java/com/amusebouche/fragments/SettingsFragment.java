package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.amusebouche.activities.MainActivity;
import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.activities.R;
import com.amusebouche.services.AppData;
import com.amusebouche.services.DatabaseHelper;

/**
 * Information fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains settings information.
 *
 * Related layouts:
 * - Content: fragment_settings.xml
 */
public class SettingsFragment extends Fragment {

    // Parent activity
    private MainActivity mActivity;

    // UI
    private TextView mSelectedLanguageTextView;

    // Services
    private DatabaseHelper mDatabaseHelper;


    // LIFECYCLE METHODS

    /**
     * Called when a fragment is first attached to its activity.
     *
     * @param activity Fragment activity (DetailActivity)
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

        mActivity = (MainActivity) getActivity();
        mDatabaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
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

        ScrollView mLayout = (ScrollView) inflater.inflate(R.layout.fragment_settings,
                container, false);

        // Get preferences
        String downloadImagesString = mDatabaseHelper.getAppData(AppData.PREFERENCE_DOWNLOAD_IMAGES);
        String recognizerLanguageString = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE);
        boolean downloadImagesSetting = downloadImagesString.equals(AppData.PREFERENCE_TRUE_VALUE);
        boolean recognizerLanguageSetting = recognizerLanguageString.equals(AppData.PREFERENCE_TRUE_VALUE);

        // Get views and set its values
        Switch downloadImagesSwitch = (Switch) mLayout.findViewById(R.id.setting_download_images_enabled);
        downloadImagesSwitch.setChecked(downloadImagesSetting);

        Switch recognizerLanguageSwitch = (Switch) mLayout.findViewById(R.id.setting_recognizer_language_enabled);
        recognizerLanguageSwitch.setChecked(recognizerLanguageSetting);

        RelativeLayout languageSetting = (RelativeLayout) mLayout.findViewById(R.id.setting_recipes_languages_item);
        mSelectedLanguageTextView = (TextView) mLayout.findViewById(R.id.setting_recipes_language_selected);
        setSelectedLanguages();

        // Listeners
        downloadImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDatabaseHelper.setAppData(AppData.PREFERENCE_DOWNLOAD_IMAGES,
                    isChecked ? AppData.PREFERENCE_TRUE_VALUE : AppData.PREFERENCE_FALSE_VALUE);
            }
        });

        recognizerLanguageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDatabaseHelper.setAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE,
                    isChecked ? AppData.PREFERENCE_TRUE_VALUE : AppData.PREFERENCE_FALSE_VALUE);
            }
        });

        languageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog languages = new LanguagesDialog(getActivity(), mDatabaseHelper);

                languages.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setSelectedLanguages();
                    }
                });

                languages.show();
            }
        });

        return mLayout;
    }

    /**
     * Show preferences languages in text view
     */
    private void setSelectedLanguages() {
        // Get language from shared preferences
        String language = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);

        if (!language.equals("")) {
            mSelectedLanguageTextView.setText(language);
            mActivity.reloadRecipesLanguage();
        }
    }

}
