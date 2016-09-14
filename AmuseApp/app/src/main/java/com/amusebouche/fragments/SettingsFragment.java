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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amusebouche.activities.MainActivity;
import com.amusebouche.data.Ingredient;
import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.activities.R;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.AppData;
import com.amusebouche.services.CustomDateFormat;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.RequestHandler;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private MainActivity mMainActivity;

    // UI
    private View mLayout;
    private TextView mSelectedLanguageTextView;
    private Snackbar mSnackBar;

    // Services
    private DatabaseHelper mDatabaseHelper;
    private TextToSpeech mTTS;
    private AmuseAPI mAPI;

    // Data
    private boolean mOfflineModeSetting;
    private boolean mWifiModeSetting;
    private boolean mRecognizerLanguageSetting;
    private String mLanguage;
    private Integer mCurrentPage;
    private String mLastUpdateDate;
    private String mNewUpdateDate;
    private boolean mIngredientsSuccess;
    private String mIngredientsError;

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

        mMainActivity = (MainActivity) getActivity();
        mDatabaseHelper = new DatabaseHelper(getActivity().getApplicationContext());

        // Get preferences
        String offlineModeString = mDatabaseHelper.getAppData(AppData.PREFERENCE_OFFLINE_MODE);
        String wifiModeString = mDatabaseHelper.getAppData(AppData.PREFERENCE_WIFI_MODE);
        String recognizerLanguageString = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE);
        mOfflineModeSetting = offlineModeString.equals(AppData.PREFERENCE_TRUE_VALUE);
        mWifiModeSetting = wifiModeString.equals(AppData.PREFERENCE_TRUE_VALUE);
        mRecognizerLanguageSetting = recognizerLanguageString.equals(AppData.PREFERENCE_TRUE_VALUE);
        mLanguage = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);
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

        // Get views and set its values
        View offlineModeView = mLayout.findViewById(R.id.setting_offline_mode_item);
        final Switch offlineModeSwitch = (Switch) mLayout.findViewById(R.id.setting_offline_mode_enabled);
        offlineModeSwitch.setChecked(mOfflineModeSetting);

        View wifiModeView = mLayout.findViewById(R.id.setting_wifi_mode_item);
        final Switch wifiModeSwitch = (Switch) mLayout.findViewById(R.id.setting_wifi_mode_enabled);
        wifiModeSwitch.setChecked(mWifiModeSetting);

        View recognizerLanguageView = mLayout.findViewById(R.id.setting_recognizer_language_item);
        final Switch recognizerLanguageSwitch = (Switch) mLayout.findViewById(
            R.id.setting_recognizer_language_enabled);
        recognizerLanguageSwitch.setChecked(mRecognizerLanguageSetting);

        RelativeLayout languageSetting = (RelativeLayout) mLayout.findViewById(
            R.id.setting_recipes_languages_item);
        mSelectedLanguageTextView = (TextView) mLayout.findViewById(
            R.id.setting_recipes_language_selected);
        setSelectedLanguage();

        View downloadLanguages = mLayout.findViewById(R.id.setting_download_languages_item);
        View downloadIngredients = mLayout.findViewById(R.id.setting_download_ingredients_item);

        // Listeners
        offlineModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offlineModeSwitch.setChecked(!offlineModeSwitch.isChecked());
                mOfflineModeSetting = offlineModeSwitch.isChecked();
                mDatabaseHelper.setAppData(AppData.PREFERENCE_OFFLINE_MODE,
                        mOfflineModeSetting ? AppData.PREFERENCE_TRUE_VALUE :
                        AppData.PREFERENCE_FALSE_VALUE);
                mMainActivity.updateOfflineModeSetting();
            }
        });

        wifiModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiModeSwitch.setChecked(!wifiModeSwitch.isChecked());
                mWifiModeSetting = offlineModeSwitch.isChecked();
                mDatabaseHelper.setAppData(AppData.PREFERENCE_WIFI_MODE,
                        mWifiModeSetting ? AppData.PREFERENCE_TRUE_VALUE :
                        AppData.PREFERENCE_FALSE_VALUE);
                mMainActivity.updateWifiModeSetting();
            }
        });

        recognizerLanguageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizerLanguageSwitch.setChecked(!recognizerLanguageSwitch.isChecked());
                mRecognizerLanguageSetting = recognizerLanguageSwitch.isChecked();
                mDatabaseHelper.setAppData(AppData.PREFERENCE_RECOGNIZER_LANGUAGE,
                        mRecognizerLanguageSetting ? AppData.PREFERENCE_TRUE_VALUE :
                        AppData.PREFERENCE_FALSE_VALUE);
            }
        });

        languageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog languages = new LanguagesDialog(getActivity(), mDatabaseHelper, false);

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

        downloadIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPage = 1;
                preLoadIngredients();
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
            mMainActivity.reloadRecipesLanguage();
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

                        Snackbar.make(mLayout, getString(R.string.settings_language_downloaded_message),
                            Snackbar.LENGTH_LONG)
                            .show();
                    } else {
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

    private void showLoading() {
        // Show loading view
        ProgressBar progressBar = new ProgressBar(mMainActivity);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF,
            android.graphics.PorterDuff.Mode.MULTIPLY);

        mSnackBar = Snackbar.make(mLayout,
            getString(R.string.splash_screen_loading_ingredients_message), Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snack_view = (Snackbar.SnackbarLayout) mSnackBar.getView();
        snack_view.addView(progressBar);
        mSnackBar.show();
    }

    // INGREDIENTS

    /**
     * Before load ingredients page by page.
     * Shows loading indicator. Calculates dates. Generates a new retrofit service.
     */
    private void preLoadIngredients() {
        mIngredientsSuccess = false;

        if (mOfflineModeSetting) {
            mIngredientsError = getString(R.string.settings_ingredients_downloaded_offline_mode_message);
            finishDownload();
        } else {
            // Set info message
            showLoading();

            String lastUpdate = mDatabaseHelper.getAppData(AppData.INGREDIENTS_LAST_UPDATE + mLanguage);
            mLastUpdateDate = lastUpdate.equals("") ? null : lastUpdate;

            // Prepare new update date as now
            mNewUpdateDate = CustomDateFormat.getUTCString(new Date());

            // Create a new retrofit service and load ingredients
            mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            loadIngredients();
        }
    }

    /**
     * Send requests to the API to get the ingredients and store them into the database.
     */
    private void loadIngredients() {
        if (!mWifiModeSetting || RequestHandler.isWifiConnected(mMainActivity)) {
            Call<ResponseBody> call = mAPI.getIngredients(mCurrentPage, mLanguage, mLastUpdateDate);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    setData(response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // End
                    finishDownload();
                }
            });
        } else {
            mIngredientsError = getString(R.string.settings_ingredients_downloaded_wifi_mode_message);
            finishDownload();
        }
    }

    /**
     * Auxiliar method to help process and store every ingredient into the database.
     */
    private void setData(Response<ResponseBody> response) {

        if (response.code() == 200) {
            String data = "";

            // Get response data
            if (response.body() != null) {
                try {
                    data = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Build objects from response data
            if (!data.equals("")) {
                try {
                    JSONObject jObject = new JSONObject(data);
                    JSONArray results = jObject.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        Ingredient ing = new Ingredient(results.getJSONObject(i));

                        if (mDatabaseHelper.existIngredient(ing)) {
                            mDatabaseHelper.updateIngredient(ing);
                        } else {
                            mDatabaseHelper.createIngredient(ing);
                        }
                    }

                    if (jObject.getString("next").equals("null")) {
                        // Set new update date as last one
                        mDatabaseHelper.setAppData(AppData.INGREDIENTS_LAST_UPDATE + mLanguage,
                            mNewUpdateDate);

                        // Go to next step
                        mIngredientsSuccess = true;
                        finishDownload();
                    } else {
                        // Load next page of ingredients
                        mCurrentPage += 1;
                        loadIngredients();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // End
                    finishDownload();
                }
            } else {
                finishDownload();
            }
        } else {
            finishDownload();
        }
    }

    private void finishDownload() {
        if (mSnackBar != null) {
            mSnackBar.dismiss();
            mSnackBar = null;
        }

        if (mIngredientsSuccess) {
            Snackbar.make(mLayout, getString(R.string.settings_ingredients_downloaded_message),
                Snackbar.LENGTH_LONG)
                .show();
        } else {

            Snackbar.make(mLayout, mIngredientsError.length() > 0 ? mIngredientsError :
                getString(R.string.settings_ingredients_downloaded_error),
                Snackbar.LENGTH_LONG).show();
        }
    }
}
