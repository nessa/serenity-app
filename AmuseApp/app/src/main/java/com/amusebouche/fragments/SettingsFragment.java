package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.activities.R;
import com.amusebouche.services.AppData;

import java.util.ArrayList;
import java.util.Arrays;

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

    private SharedPreferences mSharedPreferences;

    private TextView mSelectedLanguagesTextView;

    private String SEPARATOR = ",";

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

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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
        boolean downloadImagesSetting = mSharedPreferences.getBoolean(
                AppData.PREFERENCE_DOWNLOAD_IMAGES, false);
        boolean recognizerLanguageSetting = mSharedPreferences.getBoolean(
                AppData.PREFERENCE_RECOGNIZER_LANGUAGE, false);

        // Get views and set its values
        Switch downloadImagesSwitch = (Switch) mLayout.findViewById(R.id.setting_download_images_enabled);
        downloadImagesSwitch.setChecked(downloadImagesSetting);

        Switch recognizerLanguageSwitch = (Switch) mLayout.findViewById(R.id.setting_recognizer_language_enabled);
        recognizerLanguageSwitch.setChecked(recognizerLanguageSetting);

        RelativeLayout languageSetting = (RelativeLayout) mLayout.findViewById(R.id.setting_recipes_languages_item);
        mSelectedLanguagesTextView = (TextView) mLayout.findViewById(R.id.setting_recipes_languages_selected);
        setSelectedLanguages();

        // Listeners
        downloadImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(AppData.PREFERENCE_DOWNLOAD_IMAGES, isChecked);
                editor.apply();
            }
        });

        recognizerLanguageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(AppData.PREFERENCE_RECOGNIZER_LANGUAGE, isChecked);
                editor.apply();
            }
        });

        languageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog languages = new LanguagesDialog(getActivity(), mSharedPreferences);

                languages.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d("SETTINGS", "ON DISMISS");
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
        // Get languages from shared preferences
        String languages = mSharedPreferences.getString(
                AppData.PREFERENCE_RECIPES_LANGUAGE, "");

        if (languages.length() > 0) {
            ArrayList<String> selectedLanguages = new ArrayList<>(Arrays.asList(languages.split(SEPARATOR)));

            String lang = "";
            int count = 0;

            for (String l : selectedLanguages) {
                if (count > 0) {
                    lang += ", ";
                }

                lang += l;
                count++;
            }

            mSelectedLanguagesTextView.setText(lang);
        }
    }

}
