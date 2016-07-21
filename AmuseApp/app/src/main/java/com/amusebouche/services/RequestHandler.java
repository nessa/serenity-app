package com.amusebouche.services;

import android.util.Pair;

import java.util.ArrayList;

/**
 * RequestHandler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * This class helps build URLs and parameters string to use with AmuseAPI (retrofit implementation).
 */
public class RequestHandler {

    // Basic
    public static String API_GET_SEPARATOR = "?";
    public static String API_PARAM_SEPARATOR = "&";
    public static String API_PARAM_EQUAL = "=";
    public static String API_PARAM_NOT_EQUAL = "!=";

    // Endpoints
    public static String API_RECIPES_ENDPOINT = "recipes/";

    // Basic filters
    public static String API_PARAM_PAGE = "page";
    public static String API_PARAM_PAGE_SIZE = "page_size";

    // Recipe filters
    public static String API_PARAM_TITLE = "title";
    public static String API_PARAM_CATEGORY = "category";
    public static String API_PARAM_DIFFICULTY = "difficulty";
    public static String API_PARAM_TYPE_OF_DISH = "type_of_dish";
    public static String API_PARAM_INGREDIENT = "ingredient";
    public static String API_PARAM_LANGUAGE = "language";
    public static String API_PARAM_RATING = "average_rating";
    public static String API_PARAM_ORDERING = "ordering";
    public static String API_PARAM_UPDATED_TIMESTAMP = "updated_timestamp";
    public static String API_PARAM_DISLIKE_PREFIX = "dislike_";
    public static String API_PARAM_DISLIKE_CATEGORY = "dislike_category";
    public static String API_PARAM_DISLIKE_INGREDIENT = "dislike_ingredient";


    /**
     * Generates an URL given and endpoint and it parameter's string
     * @param endpoint Endpoint
     * @param params Parameter's string
     * @return URL
     */
    static public String buildGetURL(String endpoint, String params) {
        return RetrofitServiceGenerator.API_BASE_URL + endpoint + API_GET_SEPARATOR + params +
            API_PARAM_SEPARATOR;
    }

    /**
     * Build encoded strings with given parameters
     * @param page External attribute value for page parameter
     * @param params Rest of parameters
     * @return Parameter's string
     */
    static public String buildParams(int page, ArrayList<Pair<String, ArrayList<String>>> params) {
        String paramString = String.format("%s%s%d", API_PARAM_PAGE, API_PARAM_EQUAL, page);

        if (params != null) {
            for (Pair item : params) {
                String key = (String) item.first;

                for (String value : (ArrayList<String>) item.second) {
                    paramString += API_PARAM_SEPARATOR;

                    if (key.startsWith(API_PARAM_DISLIKE_PREFIX)) {

                        if (key.equals(API_PARAM_DISLIKE_CATEGORY)) {
                            paramString += API_PARAM_CATEGORY;
                        }

                        if (key.equals(API_PARAM_DISLIKE_INGREDIENT)) {
                            paramString += API_PARAM_INGREDIENT;
                        }

                        paramString += API_PARAM_NOT_EQUAL;
                    } else {
                        paramString += key;
                        paramString += API_PARAM_EQUAL;
                    }

                    paramString += value;
                }
            }
        }

        return paramString;
    }
}
