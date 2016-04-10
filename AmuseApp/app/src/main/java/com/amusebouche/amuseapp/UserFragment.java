package com.amusebouche.amuseapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amusebouche.services.ServiceHandler;
import com.securepreferences.SecurePreferences;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Information fragment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android fragment class, part of main activity.
 * It contains user information if its logged in or login view
 * if not (using a dialog).
 *
 * Related layouts:
 * - Content: fragment_user.xml
 */
public class UserFragment extends Fragment {

    // Data variables
    private int mStatus;

    private String mUsername = "";
    private String mPassword;
    private boolean mRemember;
    private String mName;
    private String mMail;

    private List<NameValuePair> mSignUpData;

    private String mLoginSuccessMessage = "";
    private String mLoginErrorMessage = "";
    private String mSignUpErrorMessage = "";

    // Validation
    private boolean mLoginUsernameValid;
    private boolean mLoginPasswordValid;
    private boolean mSignUpUsernameValid;
    private boolean mSignUpEmailValid;
    private boolean mSignUpNameValid;
    private boolean mSignUpSurnameValid;
    private boolean mSignUpPasswordValid;
    private boolean mSignUpRepeatPasswordValid;

    // UI variables
    private RelativeLayout mLoginView;
    private RelativeLayout mSignUpView;
    private LinearLayout mUserView;
    private LinearLayout mLoadingIndicator;

    private Button mLoginButton;
    private Button mSignUpButton;

    // Preferences
    private SharedPreferences mSharedPreferences;

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

        mSharedPreferences = new SecurePreferences(getActivity(), "", "user_preferences.xml");
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

        RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_user, container, false);

        mLoginView = (RelativeLayout) mLayout.findViewById(R.id.login);
        mSignUpView = (RelativeLayout) mLayout.findViewById(R.id.sign_up);
        mUserView = (LinearLayout) mLayout.findViewById(R.id.user);
        mLoadingIndicator = (LinearLayout) mLayout.findViewById(R.id.loading_indicator);

        // Show loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Check if user is logged in
        String mAuthToken = mSharedPreferences.getString(getString(R.string.preference_auth_token), "");
        if (mAuthToken.equals("")) {
            showLoginView();
        } else {
            new GetUser().execute();
        }
        
        return mLayout;
    }


    // Views

    /**
     * Set login view content and show it
     */
    private void showLoginView() {
        final EditText usernameTextView = (EditText) mLoginView.findViewById(R.id.username);
        final EditText passwordTextView = (EditText) mLoginView.findViewById(R.id.password);
        passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Validations
        usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mLoginUsernameValid = checkRequiredValidation(usernameTextView);
                toggleLoginButton();
            }
        });

        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mLoginPasswordValid = checkRequiredValidation(passwordTextView);
                toggleLoginButton();
            }
        });

        // Remember password?
        final CheckBox rememberCheckBox = (CheckBox) mLoginView.findViewById(R.id.remember);
        rememberCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemember = rememberCheckBox.isChecked();
            }
        });

        // Hide/show password
        Button changePasswordInputTypeButton = (Button) mLoginView.findViewById(R.id.change_password_input_type);
        changePasswordInputTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passwordTextView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }

                // Set cursor at the end
                passwordTextView.setSelection(passwordTextView.getText().length());
            }
        });

        // Login functionality
        mLoginButton = (Button) mLoginView.findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = usernameTextView.getText().toString();
                mPassword = passwordTextView.getText().toString();

                // Set preferences if needed
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(getString(R.string.preference_remember), mRemember);
                if (mRemember) {
                    editor.putString(getString(R.string.preference_username), mUsername);
                    editor.putString(getString(R.string.preference_password), mPassword);
                } else {
                    editor.putString(getString(R.string.preference_username), "");
                    editor.putString(getString(R.string.preference_password), "");
                }
                editor.apply();

                // Show indicator and launch login
                mLoginView.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new Login().execute();
            }
        });

        // Go to sign up view functionality
        TextView signUp = (TextView) mLoginView.findViewById(R.id.go_to_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpView();
            }
        });

        // Set initial data
        mRemember = mSharedPreferences.getBoolean(getString(R.string.preference_remember), false);
        rememberCheckBox.setChecked(mRemember);

        if (mRemember) {
            mUsername = mSharedPreferences.getString(getString(R.string.preference_username), "");
            mPassword = mSharedPreferences.getString(getString(R.string.preference_password), "");

            usernameTextView.setText(mUsername);
            passwordTextView.setText(mPassword);
        } else {
            usernameTextView.setText("");
            passwordTextView.setText("");
        }

        // Initial validation
        mLoginUsernameValid = checkRequiredValidation(usernameTextView);
        mLoginPasswordValid = checkRequiredValidation(passwordTextView);
        toggleLoginButton();

        // Set error if it's defined
        if (!mLoginErrorMessage.equals("")) {
            TextView errorTextView = (TextView) mLoginView.findViewById(R.id.login_error_message);
            errorTextView.setText(mLoginErrorMessage);
        }

        // Set success message if it's defined
        if (!mLoginSuccessMessage.equals("")) {
            TextView successTextView = (TextView) mLoginView.findViewById(R.id.login_success_message);
            successTextView.setText(mLoginSuccessMessage);
        }

        // Show login view
        mLoadingIndicator.setVisibility(View.GONE);
        mSignUpView.setVisibility(View.GONE);
        mLoginView.setVisibility(View.VISIBLE);
    }

    /**
     * Set sign up view content and show it
     */
    private void showSignUpView() {
        final EditText usernameTextView = (EditText) mSignUpView.findViewById(R.id.sign_up_username);
        final EditText emailTextView = (EditText) mSignUpView.findViewById(R.id.sign_up_email);
        final EditText nameTextView = (EditText) mSignUpView.findViewById(R.id.sign_up_name);
        final EditText surnameTextView = (EditText) mSignUpView.findViewById(R.id.sign_up_surname);
        final EditText passwordTextView = (EditText) mSignUpView.findViewById(R.id.sign_up_password);
        final EditText repeatPasswordTextView = (EditText) mSignUpView.findViewById(R.id.sign_up_repeat_password);

        passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repeatPasswordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Validation
        usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSignUpUsernameValid = checkRequiredValidation(usernameTextView);
                toggleSignUpButton();
            }
        });

        emailTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSignUpEmailValid = checkRequiredValidation(emailTextView);

                if (mSignUpEmailValid) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTextView.getText()).matches()) {
                        emailTextView.setError(getString(R.string.users_error_wrong_email));

                        mSignUpEmailValid = false;
                    }
                }

                toggleSignUpButton();
            }
        });

        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mSignUpNameValid = checkRequiredValidation(nameTextView);
                toggleSignUpButton();
            }
        });

        surnameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mSignUpSurnameValid = checkRequiredValidation(surnameTextView);
                toggleSignUpButton();
            }
        });

        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mSignUpPasswordValid = checkRequiredValidation(passwordTextView);

                if (mSignUpRepeatPasswordValid) {
                    if (!passwordTextView.getText().toString().equals(repeatPasswordTextView.getText().toString())) {
                        repeatPasswordTextView.setError(getString(R.string.users_error_passwords_dont_match));

                        mSignUpRepeatPasswordValid = false;
                    }
                }

                toggleSignUpButton();
            }
        });

        repeatPasswordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSignUpRepeatPasswordValid = checkRequiredValidation(repeatPasswordTextView);

                if (mSignUpRepeatPasswordValid) {
                    if (!passwordTextView.getText().toString().equals(repeatPasswordTextView.getText().toString())) {
                        repeatPasswordTextView.setError(getString(R.string.users_error_passwords_dont_match));

                        mSignUpRepeatPasswordValid = false;
                    }
                }

                toggleSignUpButton();
            }
        });

        // Show/hide passwords
        Button changePasswordInputTypeButton = (Button) mSignUpView.findViewById(R.id.change_sign_up_password_input_type);
        Button changeRepeatPasswordInputTypeButton = (Button) mSignUpView.findViewById(R.id.change_sign_up_repeat_password_input_type);

        changePasswordInputTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passwordTextView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }

                // Set cursor at the end
                passwordTextView.setSelection(passwordTextView.getText().length());
            }
        });

        changeRepeatPasswordInputTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatPasswordTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    repeatPasswordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    repeatPasswordTextView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }

                // Set cursor at the end
                repeatPasswordTextView.setSelection(repeatPasswordTextView.getText().length());
            }
        });


        // Sign up functionality
        mSignUpButton = (Button) mSignUpView.findViewById(R.id.sign_up_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = usernameTextView.getText().toString();
                mPassword = passwordTextView.getText().toString();

                mSignUpData = new ArrayList<>();
                mSignUpData.add(new BasicNameValuePair("username", usernameTextView.getText().toString()));
                mSignUpData.add(new BasicNameValuePair("email", emailTextView.getText().toString()));
                mSignUpData.add(new BasicNameValuePair("name", nameTextView.getText().toString()));
                mSignUpData.add(new BasicNameValuePair("surname", surnameTextView.getText().toString()));
                mSignUpData.add(new BasicNameValuePair("birthday", "1900-01-01"));
                mSignUpData.add(new BasicNameValuePair("password", passwordTextView.getText().toString()));

                // Show indicator and launch login
                mSignUpView.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new Register().execute();
            }
        });


        // Go to login view functionality
        TextView login = (TextView) mSignUpView.findViewById(R.id.go_to_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginView();
            }
        });

        // Initial validation
        mSignUpUsernameValid = checkRequiredValidation(usernameTextView);
        mSignUpEmailValid = checkRequiredValidation(emailTextView);
        mSignUpNameValid = checkRequiredValidation(nameTextView);
        mSignUpSurnameValid = checkRequiredValidation(surnameTextView);
        mSignUpPasswordValid = checkRequiredValidation(passwordTextView);
        mSignUpRepeatPasswordValid = checkRequiredValidation(repeatPasswordTextView);
        toggleSignUpButton();

        // Set error if it's defined
        if (!mSignUpErrorMessage.equals("")) {
            TextView errorTextView = (TextView) mSignUpView.findViewById(R.id.sign_up_error_message);
            errorTextView.setText(mSignUpErrorMessage);
        }

        // Show login view
        mLoadingIndicator.setVisibility(View.GONE);
        mLoginView.setVisibility(View.GONE);
        mSignUpView.setVisibility(View.VISIBLE);
    }

    /**
     * Set user view content and show it
     */
    private void showUserView() {

        final TextView mNameTextView = (TextView) mUserView.findViewById(R.id.name);
        final TextView mMailTextView = (TextView) mUserView.findViewById(R.id.email);

        mNameTextView.setText(mName);
        mMailTextView.setText(mMail);

        Button logoutButton = (Button) mUserView.findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        mLoadingIndicator.setVisibility(View.GONE);
        mUserView.setVisibility(View.VISIBLE);
    }


    // Log out

    /**
     * Log out (not real request).
     * It only removes the token.
     */
    private void logOut() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.preference_auth_token), "");
        editor.apply();

        mUserView.setVisibility(View.GONE);
        mLoginView.setVisibility(View.VISIBLE);
    }


    // Validations

    /**
     * Check if a text view has text
     * @param textView Text view to check
     * @return A boolean than indicates if the validations is correct
     */
    private boolean checkRequiredValidation(TextView textView) {
        if (textView.getText().toString().length() == 0) {
            textView.setError(getString(R.string.users_error_required));
            return false;
        }

        return true;
    }

    /**
     * Enable or disable login button depending on its text views' validations
     */
    private void toggleLoginButton() {
        mLoginButton.setEnabled(mLoginUsernameValid && mLoginPasswordValid);
    }

    /**
     * Enable or disable sign up button depending on its text views' validations
     */
    private void toggleSignUpButton() {
        mSignUpButton.setEnabled(mSignUpUsernameValid && mSignUpEmailValid && mSignUpNameValid &&
                mSignUpSurnameValid && mSignUpPasswordValid && mSignUpRepeatPasswordValid);
    }


    /**
     * Async task class to log the user in
     */
    private class Login extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoginErrorMessage = "";
            mLoginSuccessMessage = "";
        }

        @Override
        protected Void doInBackground(Void... result) {

            // Create service handler class instance
            ServiceHandler mServiceHandler = new ServiceHandler(getActivity(), mSharedPreferences);

            // Check internet connection
            if (mServiceHandler.checkInternetConnection()) {
                // Login
                mStatus = mServiceHandler.login(mUsername, mPassword);
                Log.d("STATUS", String.format("%d", mStatus));
            } else {
                Log.d("ERROR", "NO INTERNET CONNECTION");
                mStatus = -1;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mStatus == 200) {
                new GetUser().execute();
            } else {
                if (mStatus == -1) {
                    mLoginErrorMessage = getString(R.string.users_login_error_no_connection);
                } else {
                    mLoginErrorMessage = getString(R.string.users_login_error_wrong_credentials);
                }

                // Show error
                showLoginView();
            }
        }
    }


    /**
     * Async task class to register the user
     */
    private class Register extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mSignUpErrorMessage = "";
        }

        @Override
        protected Void doInBackground(Void... result) {

            // Create service handler class instance
            ServiceHandler mServiceHandler = new ServiceHandler(getActivity(), mSharedPreferences);

            // Check internet connection
            if (mServiceHandler.checkInternetConnection()) {

                // Make a request to url and getting response
                String jsonStr = mServiceHandler.makeServiceCall(
                        getActivity().getResources().getString(R.string.API_USERS_ENDPOINT),
                        ServiceHandler.POST, mSignUpData);

                mStatus = mServiceHandler.getStatus();

                // Get errors from API response if needed
                if (jsonStr != null && mStatus != 201) {
                    try {
                        JSONObject jObject = new JSONObject(jsonStr);

                        if (jObject.has("username")) {
                            JSONArray array = jObject.getJSONArray("username");

                            for (int i = 0; i < array.length(); i++) {
                                if (array.get(i).equals("This field must be unique.")) {
                                    mSignUpErrorMessage = getString(R.string.users_sing_up_error_username_not_unique);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mStatus = -1;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (mStatus == 201) {
                // Show success message
                mLoginSuccessMessage = getString(R.string.users_sign_up_success);

                // Go to login view
                showLoginView();
            } else {
                if (mStatus == -1) {
                    mSignUpErrorMessage = getString(R.string.users_login_error_no_connection);
                }

                // Show error
                showSignUpView();
            }
        }
    }

    /**
     * Async task class to get the user data
     */
    private class GetUser extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... result) {

            // Create service handler class instance
            ServiceHandler mServiceHandler = new ServiceHandler(getActivity(), mSharedPreferences);

            // Check internet connection
            if (mServiceHandler.checkInternetConnection()) {
                if (mUsername.equals("")) {
                    mUsername = mSharedPreferences.getString(getString(R.string.preference_username), "");
                }

                // Make a request to url and getting response
                String jsonStr = mServiceHandler.makeServiceCall(
                        getString(R.string.API_USERS_ENDPOINT) + getString(R.string.API_GET_SEPARATOR) +
                        getString(R.string.API_PARAM_USERNAME) + getString(R.string.API_PARAM_EQUAL) +
                        mUsername, ServiceHandler.GET);

                mStatus = mServiceHandler.getStatus();

                if (jsonStr != null && mStatus == 200) {
                    try {
                        JSONObject jObject = new JSONObject(jsonStr);
                        JSONArray results = jObject.getJSONArray("results");

                        mName = String.format("%s %s", results.getJSONObject(0).getString("name"),
                                results.getJSONObject(0).getString("surname"));
                        mMail = results.getJSONObject(0).getString("email");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mStatus == 200) {
                showUserView();
            } else {
                // Login
                showLoginView();
            }
        }
    }
}
