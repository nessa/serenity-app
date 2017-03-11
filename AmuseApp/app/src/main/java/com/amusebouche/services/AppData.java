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

    // Files
    public static String RECIPE_TRANSITION_IMAGE = "presentRecipeImage.png";

    // Intents keys
    public static String INTENT_KEY_RECIPE = "recipe";
    public static String INTENT_KEY_RECIPE_POSITION = "recipe_position";
    public static String INTENT_KEY_RECIPE_DELETED = "recipe_deleted";
    public static String INTENT_KEY_DETAIL_TAG = "detail_tag";
    public static String INTENT_KEY_EDITION_TAG = "edition_tag";
    public static String INTENT_KEY_COUNTDOWN_TICK = "countdown_tick";
    public static String INTENT_KEY_COUNTDOWN_TICK_TIME = "countdown_tick_time";
    public static String INTENT_KEY_COUNTDOWN_FINISHED = "countdown_finished";
    public static String INTENT_KEY_COUNTDOWN_ALARM_BEEPING = "countdown_alarm_beeping";
    public static String INTENT_KEY_COUNTDOWN_ALARM_STOP = "countdown_alarm_stop";

    // Requests' codes
    public static int REQUEST_FROM_DETAIL_TO_LIST_CODE = 10;
    public static int REQUEST_FROM_EDITION_TO_DETAIL_CODE = 20;

    // Preferences
    public static String PREFERENCE_OFFLINE_MODE = "offline_mode";
    public static String PREFERENCE_WIFI_MODE = "wifi_mode";
    public static String PREFERENCE_RECIPES_LANGUAGE = "recipes_language";
    public static String PREFERENCE_RECOGNIZER_LANGUAGE = "recognizer_language";

    // App info
    public static String INGREDIENTS_LAST_UPDATE = "ingredients_last_update_";

    public static String BROADCAST_ACTION = "com.amusebouche.broadcast.countdown";

    // URLs
    public static String PRIVACY_POLICY_URL = "http://noeliarcado.es/amuse-bouche-privacy-policy/";
    public static String DONATE_URL = "https://www.paypal.me/noeliarcado";

    // Menu
    public static final int PROFILE = 0;
    public static final int NEW_RECIPES = 1;
    public static final int DOWNLOADED_RECIPES = 2;
    public static final int MY_RECIPES = 3;
    public static final int SETTINGS = 4;
    public static final int INFO = 5;

    // User data
    public static String USER_AUTH_TOKEN = "user_auth_token";
    public static String USER_REMEMBER_CREDENTIALS = "user_remember_credentials";
    public static String USER_USERNAME = "user_username";
    public static String USER_PASSWORD = "user_password";
    public static String USER_SHOW_TEXT = "user_show_text";
    public static String USER_LOGGED_USERNAME = "user_logged_username";

    // Languages
    public static ArrayList<Pair<String, Integer>> LANGUAGES = new ArrayList<>(Arrays.asList(
            new Pair<>("es", R.string.language_es),
            new Pair<>("en", R.string.language_en)
    ));

    // Locale data
    public static ArrayList<Pair<String, String>> LOCALE_COUNTRIES = new ArrayList<>(Arrays.asList(
        new Pair<>("es", "ES"),
        new Pair<>("en", "US")
    ));

    public static ArrayList<Pair<String, String>> LOCALE_DIRECTION_STRINGS = new ArrayList<>(Arrays.asList(
            new Pair<>("es", "PASO %1$d"),
            new Pair<>("en", "DIRECTION %1$d")
    ));

    public static ArrayList<Pair<String, String>> LOCALE_TIMER_INIT_STRINGS = new ArrayList<>(Arrays.asList(
            new Pair<>("es", "Iniciando temporizador"),
            new Pair<>("en", "Starting timer")
    ));

    public static ArrayList<Pair<String, String>> LOCALE_FINISH_STRINGS = new ArrayList<>(Arrays.asList(
            new Pair<>("es", "Fin de la receta. !A comer!"),
            new Pair<>("en", "The recipe is complete. Let us eat!")
    ));

    /**
     * Get locale country for a given code
     * @param code Language code
     * @return String for locale country
     */
    public static String getLocaleCountryFromCode(String code) {
        for (int i = 0; i < AppData.LOCALE_COUNTRIES.size(); i++) {
            if (AppData.LOCALE_COUNTRIES.get(i).first.equals(code.toLowerCase())) {
                return AppData.LOCALE_COUNTRIES.get(i).second;
            }
        }

        return "";
    }

    /**
     * Get locale direction string for a given code
     * @param code Language code
     * @return String for locale direction string
     */
    public static String getLocaleDirectionFromCode(String code) {
        for (int i = 0; i < AppData.LOCALE_DIRECTION_STRINGS.size(); i++) {
            if (AppData.LOCALE_DIRECTION_STRINGS.get(i).first.equals(code.toLowerCase())) {
                return AppData.LOCALE_DIRECTION_STRINGS.get(i).second;
            }
        }

        return "";
    }

    /**
     * Get locale timer string for a given code
     * @param code Language code
     * @return String for locale timer string
     */
    public static String getLocaleTimerInitFromCode(String code) {
        for (int i = 0; i < AppData.LOCALE_TIMER_INIT_STRINGS.size(); i++) {
            if (AppData.LOCALE_TIMER_INIT_STRINGS.get(i).first.equals(code.toLowerCase())) {
                return AppData.LOCALE_TIMER_INIT_STRINGS.get(i).second;
            }
        }

        return "";
    }

    /**
     * Get locale timer string for a given code
     * @param code Language code
     * @return String for locale finish string
     */
    public static String getLocaleFinishFromCode(String code) {
        for (int i = 0; i < AppData.LOCALE_FINISH_STRINGS.size(); i++) {
            if (AppData.LOCALE_FINISH_STRINGS.get(i).first.equals(code.toLowerCase())) {
                return AppData.LOCALE_FINISH_STRINGS.get(i).second;
            }
        }

        return "";
    }
}
