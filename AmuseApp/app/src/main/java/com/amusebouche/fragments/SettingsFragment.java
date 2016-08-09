package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amusebouche.activities.MainActivity;
import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.activities.R;
import com.amusebouche.services.AppData;
import com.amusebouche.services.DatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

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
    private View mLayout;
    private TextView mSelectedLanguageTextView;

    // Services
    private DatabaseHelper mDatabaseHelper;
    private TextToSpeech mTTS;


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

        mLayout = inflater.inflate(R.layout.fragment_settings, container, false);

        // Get preferences
        String offlineModeString = mDatabaseHelper.getAppData(AppData.PREFERENCE_OFFLINE_MODE);
        String wifiModeString = mDatabaseHelper.getAppData(AppData.PREFERENCE_WIFI_MODE);
        String recognizerLanguageString = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE);
        final boolean offlineModeSetting = offlineModeString.equals(AppData.PREFERENCE_TRUE_VALUE);
        final boolean wifiModeSetting = wifiModeString.equals(AppData.PREFERENCE_TRUE_VALUE);
        boolean recognizerLanguageSetting = recognizerLanguageString.equals(AppData.PREFERENCE_TRUE_VALUE);

        // Get views and set its values
        View offlineModeView = mLayout.findViewById(R.id.setting_offline_mode_item);
        final Switch offlineModeSwitch = (Switch) mLayout.findViewById(R.id.setting_offline_mode_enabled);
        offlineModeSwitch.setChecked(offlineModeSetting);

        View wifiModeView = mLayout.findViewById(R.id.setting_wifi_mode_item);
        final Switch wifiModeSwitch = (Switch) mLayout.findViewById(R.id.setting_wifi_mode_enabled);
        wifiModeSwitch.setChecked(wifiModeSetting);

        View recognizerLanguageView = mLayout.findViewById(R.id.setting_recognizer_language_item);
        final Switch recognizerLanguageSwitch = (Switch) mLayout.findViewById(
            R.id.setting_recognizer_language_enabled);
        recognizerLanguageSwitch.setChecked(recognizerLanguageSetting);

        RelativeLayout languageSetting = (RelativeLayout) mLayout.findViewById(
            R.id.setting_recipes_languages_item);
        mSelectedLanguageTextView = (TextView) mLayout.findViewById(
            R.id.setting_recipes_language_selected);
        setSelectedLanguage();

        View downloadLanguages = mLayout.findViewById(R.id.setting_download_languages_item);

        // Listeners
        offlineModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offlineModeSwitch.setChecked(!offlineModeSwitch.isChecked());
                mDatabaseHelper.setAppData(AppData.PREFERENCE_OFFLINE_MODE,
                    offlineModeSwitch.isChecked() ? AppData.PREFERENCE_TRUE_VALUE :
                        AppData.PREFERENCE_FALSE_VALUE);
            }
        });

        wifiModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiModeSwitch.setChecked(!wifiModeSwitch.isChecked());
                mDatabaseHelper.setAppData(AppData.PREFERENCE_WIFI_MODE,
                    wifiModeSwitch.isChecked() ? AppData.PREFERENCE_TRUE_VALUE :
                        AppData.PREFERENCE_FALSE_VALUE);
            }
        });

        recognizerLanguageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizerLanguageSwitch.setChecked(!recognizerLanguageSwitch.isChecked());
                mDatabaseHelper.setAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE,
                    recognizerLanguageSwitch.isChecked() ? AppData.PREFERENCE_TRUE_VALUE :
                        AppData.PREFERENCE_FALSE_VALUE);
            }
        });

        languageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog languages = new LanguagesDialog(getActivity(), mDatabaseHelper);

                languages.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setSelectedLanguage();
                    }
                });

                languages.show();
            }
        });

        downloadLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLanguages();
            }
        });

        return mLayout;
    }

    /**
     * Show preferences languages in text view
     */
    private void setSelectedLanguage() {
        // Get language from shared preferences
        String languageCode = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);

        if (!languageCode.equals("")) {
            String language = "";
            for (int i = 0; i < AppData.LANGUAGES.size(); i++) {
                if (AppData.LANGUAGES.get(i).first.equals(languageCode)) {
                    language = getString(AppData.LANGUAGES.get(i).second);
                    break;
                }
            }

            mSelectedLanguageTextView.setText(language);
            mActivity.reloadRecipesLanguage();
        }
    }

    /**
     * Check if user needs to download language packages
     */
    private void checkLanguages() {
        final String languageCode = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);
        String recognizerLanguageString = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE);
        final boolean recognizerLanguageSetting = recognizerLanguageString.equals(AppData.PREFERENCE_TRUE_VALUE);

        mTTS = new TextToSpeech(this.getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale loc = new Locale(languageCode.toLowerCase(),
                        AppData.getLocaleCountryFromCode(languageCode.toLowerCase()));

                    if (mTTS.isLanguageAvailable(loc) == TextToSpeech.LANG_AVAILABLE &&
                        (!recognizerLanguageSetting ||
                            mTTS.isLanguageAvailable(Locale.getDefault())  == TextToSpeech.LANG_AVAILABLE )) {
                        Log.d("SETTINGS", "SUCCESS");

                        Snackbar.make(mLayout, getString(R.string.settings_language_downloaded_message),
                            Snackbar.LENGTH_LONG)
                            .show();
                    } else {
                        Log.d("SETTINGS", "INSTALL");
                        Intent installTTSIntent = new Intent();
                        installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        ArrayList<String> languages = new ArrayList<>();
                        languages.add(languageCode.toLowerCase() + "_ " +
                            AppData.getLocaleCountryFromCode(languageCode.toLowerCase()));
                        installTTSIntent.putStringArrayListExtra(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA,
                            languages);
                        startActivity(installTTSIntent);
                    }
                }
            }
        });
    }
}
