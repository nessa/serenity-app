package com.amusebouche.fragments;

import android.app.Activity;
import android.app.Fragment;
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

import com.amusebouche.activities.R;
import com.amusebouche.services.AmuseAPI;
import com.amusebouche.services.DatabaseHelper;
import com.amusebouche.services.AppData;
import com.amusebouche.services.RetrofitServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    private String mUsername = "";
    private String mPassword;
    private boolean mRemember;
    private String mName;
    private String mMail;

    // Messages
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

    // Services
    private AmuseAPI mAPI;
    private DatabaseHelper mDatabaseHelper;

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

        RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_user, container, false);

        mLoginView = (RelativeLayout) mLayout.findViewById(R.id.login);
        mSignUpView = (RelativeLayout) mLayout.findViewById(R.id.sign_up);
        mUserView = (LinearLayout) mLayout.findViewById(R.id.user);
        mLoadingIndicator = (LinearLayout) mLayout.findViewById(R.id.loading_indicator);

        // Show loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Check if user is logged in
        String mAuthToken = mDatabaseHelper.getAppData(AppData.USER_AUTH_TOKEN);
        if (mAuthToken.equals("")) {
            showLoginView();
        } else {
            loadUser(mAuthToken);
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
                mDatabaseHelper.setAppData(AppData.USER_REMEMBER_CREDENTIALS,
                        mRemember ? "YES" : "NO");
                if (mRemember) {
                    mDatabaseHelper.setAppData(AppData.USER_USERNAME, mUsername);
                    mDatabaseHelper.setAppData(AppData.USER_PASSWORD, mPassword);
                } else {
                    mDatabaseHelper.setAppData(AppData.USER_USERNAME, "");
                    mDatabaseHelper.setAppData(AppData.USER_PASSWORD, "");
                }

                // Show indicator and launch login
                mLoginView.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);


                mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
                Call<ResponseBody> call = mAPI.login(mUsername, mPassword);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

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

                                        mDatabaseHelper.setAppData(AppData.USER_AUTH_TOKEN,
                                                token);

                                        loadUser(token);
                                    }
                                } else {
                                    // Show errors
                                    if (jObject.has("non_field_errors")) {
                                        JSONArray array = jObject.getJSONArray("non_field_errors");

                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.get(i).equals("Unable to login with provided credentials.")) {
                                                mLoginErrorMessage = getString(R.string.users_login_error_wrong_credentials);
                                            }
                                        }
                                    }

                                    showLoginView();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mLoginErrorMessage = getString(R.string.users_login_error);
                            showLoginView();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mLoginErrorMessage = getString(R.string.users_login_error);
                        showLoginView();
                    }

                });
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
        String remember = mDatabaseHelper.getAppData(AppData.USER_REMEMBER_CREDENTIALS);
        mRemember = remember.equals("YES");
        rememberCheckBox.setChecked(mRemember);

        if (mRemember) {
            mUsername = mDatabaseHelper.getAppData(AppData.USER_USERNAME);
            mPassword = mDatabaseHelper.getAppData(AppData.USER_PASSWORD);

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

                // Show indicator and launch login
                mSignUpView.setVisibility(View.GONE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                //new Register().execute();

                mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class);
                Call<ResponseBody> call = mAPI.register(usernameTextView.getText().toString(),
                    emailTextView.getText().toString(), nameTextView.getText().toString(),
                    surnameTextView.getText().toString(), passwordTextView.getText().toString());

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.code() == 201) {
                            // Show success message
                            mLoginSuccessMessage = getString(R.string.users_sign_up_success);

                            // Go to login view
                            showLoginView();
                        } else {
                            // Show register errors
                            String jsonStr = "";

                            if (response.body() != null) {
                                try {
                                    jsonStr = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    JSONObject jObject = new JSONObject(jsonStr);

                                    if (jObject.has("username")) {
                                        JSONArray array = jObject.getJSONArray("username");

                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.get(i).equals("A user with that username already exists.")) {
                                                mSignUpErrorMessage = getString(R.string.users_sing_up_error_username_not_unique);
                                            }
                                        }
                                    }

                                    if (jObject.has("password")) {
                                        JSONArray array = jObject.getJSONArray("password");

                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.get(i).equals("This field is required.")) {
                                                mSignUpErrorMessage = getString(R.string.users_sing_up_error_password_required);
                                            }
                                        }
                                    }

                                    if (jObject.has("email")) {
                                        JSONArray array = jObject.getJSONArray("email");

                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.get(i).equals("Enter a valid email address.")) {
                                                mSignUpErrorMessage = getString(R.string.users_sing_up_error_valid_email);
                                            }
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mSignUpErrorMessage = getString(R.string.users_sing_up_error);
                            }

                            showSignUpView();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show generic error
                        mSignUpErrorMessage = getString(R.string.users_sing_up_error);
                        showSignUpView();
                    }

                });
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
     * Log out.
     * It only removes the token.
     */
    private void logOut() {
        String token = mDatabaseHelper.getAppData(AppData.USER_AUTH_TOKEN);

        mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class, token);
        Call<ResponseBody> call = mAPI.logout();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    mDatabaseHelper.setAppData(AppData.USER_AUTH_TOKEN, "");

                    mUserView.setVisibility(View.GONE);
                    mLoginView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }

        });

    }

    private void loadUser(String token) {
        mAPI = RetrofitServiceGenerator.createService(AmuseAPI.class, token);
        Call<ResponseBody> call = mAPI.me();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    String jsonStr = "";

                    if (response.body() != null && response.code() == 200) {
                        try {
                            jsonStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jObject = new JSONObject(jsonStr);

                            mName = String.format("%s %s", jObject.getString("first_name"),
                                jObject.getString("last_name"));
                            mMail = jObject.getString("email");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    showUserView();
                } else {
                    showLoginView();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showLoginView();
            }

        });
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
}
