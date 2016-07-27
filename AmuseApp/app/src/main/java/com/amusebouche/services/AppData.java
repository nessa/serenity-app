package com.amusebouche.services;

import android.util.Pair;

import com.amusebouche.activities.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * AppData class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class contains all keys for preferences, user data or anything needed in several parts
 * of the app.
 */
public class AppData {

    // Values
    public static String PREFERENCE_TRUE_VALUE = "YES";
    public static String PREFERENCE_FALSE_VALUE = "NO";

    // Preferences
    public static String PREFERENCE_DOWNLOAD_IMAGES = "download_images";
    public static String PREFERENCE_RECIPES_LANGUAGE = "recipes_language";
    public static String PREFERENCE_RECOGNIZER_LANGUAGE = "recognizer_language";

    // App info
    public static String INGREDIENTS_LAST_UPDATE = "ingredients_last_update";

    // User data
    public static String USER_AUTH_TOKEN = "user_auth_token";
    public static String USER_REMEMBER_CREDENTIALS = "user_remember_credentials";
    public static String USER_USERNAME = "user_username";
    public static String USER_PASSWORD = "user_password";
    public static String USER_SHOW_TEXT = "user_show_text";

    // Languages
    public static ArrayList<Pair<String, Integer>> LANGUAGES = new ArrayList<>(Arrays.asList(
            new Pair<>("es", R.string.language_es),
            new Pair<>("en", R.string.language_en)
    ));
}
