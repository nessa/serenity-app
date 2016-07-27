package com.amusebouche.activities;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amusebouche.data.Ingredient;
import com.amusebouche.dialogs.LanguagesDialog;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.AppData;
import com.amusebouche.services.CustomDateFormat;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Splash activity class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com>
 *
 * It shows a loading indicator while:
 * - Download new ingredients.
 * - Check user login.
 */
public class SplashScreenActivity extends Activity {

    // User interface
    private ProgressBar mProgressBar;
    private TextView mTextView;

    // Data variables
    private Integer mCurrentPage;

    private DatabaseHelper mDatabaseHelper;
    private AmuseAPI mAPI;

    private String mLastUpdateDate;
    private String mNewUpdateDate;
    private String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Get language from database
        mLanguage = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);

        // If there are no set languages, ask for them
        if (mLanguage.equals("")) {
            Dialog languagesDialog = new LanguagesDialog(this, mDatabaseHelper);

            languagesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLanguage = mDatabaseHelper.getAppData(AppData.PREFERENCE_RECIPES_LANGUAGE);
                    preLoadIngredients();
                }
            });

            languagesDialog.show();
        } else {
            preLoadIngredients();
        }
    }

    /**
     * Before load ingredients page by page.
     * Shows loading indicator. Calculates dates. Generates a new retrofit service.
     */
    private void preLoadIngredients() {
        // Set info message
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText(getString(R.string.splash_screen_loading_ingredients_message));

        String lastUpdate = mDatabaseHelper.getAppData(AppData.INGREDIENTS_LAST_UPDATE);
        mLastUpdateDate = lastUpdate.equals("") ? null : lastUpdate;

        // Prepare new update date as now
        mNewUpdateDate = CustomDateFormat.getUTCString(new Date());

        // Create a new retrofit service and load ingredients
        mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
        loadIngredients();
    }

    /**
     * Send requests to the API to get the ingredients and store them into the database.
     */
    private void loadIngredients() {
        Call<ResponseBody> call = mAPI.getIngredients(mCurrentPage, mLanguage, mLastUpdateDate);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                setData(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // End
                checkIfUserIsLoggedIn();
            }
        });
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
                        mDatabaseHelper.setAppData(AppData.INGREDIENTS_LAST_UPDATE, mNewUpdateDate);

                        // Go to next step
                        checkIfUserIsLoggedIn();
                    } else {
                        // Load next page of ingredients
                        mCurrentPage += 1;
                        loadIngredients();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // End
                    checkIfUserIsLoggedIn();
                }
            } else {
                checkIfUserIsLoggedIn();
            }
        } else {
            checkIfUserIsLoggedIn();
        }
    }

    /**
     * Try to login with stored credentials (if there are)
     */
    private void checkIfUserIsLoggedIn() {
        String authToken = mDatabaseHelper.getAppData(AppData.USER_AUTH_TOKEN);

        if (authToken.equals("")) {
            // Try to login
            login();
        } else {
            // Load user with the present token
            loadUser(authToken);
        }
    }

    /**
     * Launch login request
     */
    private void login() {
        boolean remember = mDatabaseHelper.getAppData(AppData.USER_REMEMBER_CREDENTIALS).equals("YES");
        if (remember) {
            String username = mDatabaseHelper.getAppData(AppData.USER_USERNAME);
            String password = mDatabaseHelper.getAppData(AppData.USER_PASSWORD);

            mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
            Call<ResponseBody> call = mAPI.login(username, password);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    boolean error = false;
                    String jsonStr = "";
                    String token;

                    if (response.body() != null) {
                        try {
                            jsonStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jObject = new JSONObject(jsonStr);

                            if (response.code() == 200) {
                                if (jObject.has("auth_token")) {
                                    token = jObject.getString("auth_token");

                                    mDatabaseHelper.setAppData(AppData.USER_AUTH_TOKEN, token);

                                    loadUser(token);
                                }
                            } else {
                                error = true;
                            }

                        } catch (JSONException e) {
                            error = true;
                        }
                    } else {
                        error = true;
                    }

                    if (error) {
                        mDatabaseHelper.setAppData(AppData.USER_SHOW_TEXT, "");
                        goToMainView();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mDatabaseHelper.setAppData(AppData.USER_SHOW_TEXT, "");
                    goToMainView();
                }

            });
        } else {
            mDatabaseHelper.setAppData(AppData.USER_SHOW_TEXT, "");
            goToMainView();
        }
    }

    /**
     * Launch me request
     */
    private void loadUser(String token) {
        mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class, token);
        Call<ResponseBody> call = mAPI.me();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean error = false;

                if (response.code() == 200) {
                    String jsonStr = "";

                    if (response.body() != null) {
                        try {
                            jsonStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jObject = new JSONObject(jsonStr);

                            mDatabaseHelper.setAppData(AppData.USER_SHOW_TEXT,
                                    jObject.getString("email"));

                            goToMainView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            error = true;
                        }
                    } else {
                        error = true;
                    }

                } else {
                    error = true;
                }

                if (error) {
                    mDatabaseHelper.setAppData(AppData.USER_SHOW_TEXT, "");
                    goToMainView();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mDatabaseHelper.setAppData(AppData.USER_SHOW_TEXT, "");
                goToMainView();
            }

        });
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