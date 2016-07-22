package com.amusebouche.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amusebouche.data.Ingredient;
import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends Activity {

    public static String PREFERENCE_INGREDIENT_LAST_UPDATE = "PREFERENCE_INGREDIENT_LAST_UPDATE";
    public static String PREFERENCE_LANGUAGE = "PREFERENCE_LANGUAGE";

    // User interface
    private ProgressBar mProgressBar;
    private TextView mTextView;

    // Data variables
    private Integer mCurrentPage;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DatabaseHelper mDatabaseHelper;
    private SharedPreferences mSharedPreferences;

    private String mNewUpdateDate;
    private String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get preferences
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Gets the database helper to access the database for the application
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        // Set paginate variables
        mCurrentPage = 1;

        setContentView(R.layout.splash_screen);
        mTextView = (TextView) findViewById(R.id.text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get languages from shared preferences
        mLanguage = mSharedPreferences.getString(PREFERENCE_LANGUAGE, "");

        // If there are no set languages, ask for them
        if (mLanguage.equals("")) {
            Dialog languagesDialog = new LanguagesDialog(this, mSharedPreferences);

            languagesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLanguage = mSharedPreferences.getString(PREFERENCE_LANGUAGE, "");
                    loadIngredients();
                }
            });

            languagesDialog.show();
        } else {
            loadIngredients();
        }
    }

    /**
     * Send requests to the API to get the ingredients and store them into the database.
     */
    private void loadIngredients() {
        // Set info message
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText(getString(R.string.splash_screen_loading_ingredients_message));

        final String lastUpdate = mSharedPreferences.getString(PREFERENCE_INGREDIENT_LAST_UPDATE, "");

        // Prepare new update date as now
        mNewUpdateDate = dateFormat.format(new Date());

        AmuseAPI mAPI;
        if (lastUpdate.equals("")) {
            mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            Call<ResponseBody> call = mAPI.getIngredients(mCurrentPage, mLanguage);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    setData(response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // End
                    checkLogin();
                }
            });
        } else {
            mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            Call<ResponseBody> call = mAPI.getIngredients(mCurrentPage, mLanguage, lastUpdate);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    setData(response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // End
                    checkLogin();
                }
            });
        }
    }

    /**
     * Auxiliar method to help process and store every ingredient into the database.
     */
    private void setData(Response<ResponseBody> response) {

        if (response.body() != null && response.code() == 200) {
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

                    if (jObject.getString("next") == null) {
                        // Set new update date as last one
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(PREFERENCE_INGREDIENT_LAST_UPDATE, mNewUpdateDate);
                        editor.apply();

                        // Go to next step
                        checkLogin();
                    } else {
                        // Load next page of ingredients
                        mCurrentPage += 1;
                        loadIngredients();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // End
                    checkLogin();
                }
            } else {
                checkLogin();
            }
        } else {
            checkLogin();
        }
    }

    /**
     * Try to login with stored credentials (if there are)
     */
    private void checkLogin() {
        // TODO: Try to login
        Log.d("SPLASH", "LOGIN");
        mTextView.setText(getString(R.string.splash_screen_login_message));
        goToMainView();
    }

    /**
     * Final step: load main activity
     */
    private void goToMainView() {
        // Close the progress dialog
        mProgressBar.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);

        // Start the next activity
        Intent mainIntent = new Intent().setClass(
            SplashScreenActivity.this, MainActivity.class);
        startActivity(mainIntent);

        // Close the activity so the user won't be able to go back to this
        // activity pressing Back button
        finish();
    }
}