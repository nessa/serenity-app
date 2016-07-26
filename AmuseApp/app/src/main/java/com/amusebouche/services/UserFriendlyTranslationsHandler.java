package com.amusebouche.services;

import android.content.Context;
import android.util.Pair;

import com.amusebouche.activities.R;
import com.amusebouche.data.RecipeCategory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User friendly translator for recipes data.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 */
public class UserFriendlyTranslationsHandler {

    // Categories
    private static int mDefaultCategoryPosition = 0;

    private static ArrayList<Pair<String, Integer>> mCategories = new ArrayList<>(Arrays.asList(
        new Pair<>(RecipeCategory.CATEGORY_DIET_MEDITERRANEAN, R.string.category_allergy_mediterranean),
        new Pair<>(RecipeCategory.CATEGORY_DIET_VEGETARIAN, R.string.category_allergy_vegetarian),
        new Pair<>(RecipeCategory.CATEGORY_DIET_VEGAN, R.string.category_allergy_vegan),
        new Pair<>(RecipeCategory.CATEGORY_ALLERGY_GLUTEN_CODE, R.string.category_allergy_gluten),
        new Pair<>(RecipeCategory.CATEGORY_ALLERGY_LACTOSE_CODE, R.string.category_allergy_lactose),
        new Pair<>(RecipeCategory.CATEGORY_ALLERGY_SHELLFISH_CODE, R.string.category_allergy_shellfish),
        new Pair<>(RecipeCategory.CATEGORY_ALLERGY_FISH_CODE, R.string.category_allergy_fish),
        new Pair<>(RecipeCategory.CATEGORY_ALLERGY_DRIED_FRUIT_CODE, R.string.category_allergy_dried_fruit)
    ));

    // Types of dish data

    private static int mDefaultTypePosition = 5;

    private static ArrayList<Pair<String, Integer>> mTypes = new ArrayList<>(Arrays.asList(
            new Pair<>("APPETIZER", R.string.type_of_dish_appetizer),
            new Pair<>("FIRST-COURSE", R.string.type_of_dish_first_course),
            new Pair<>("SECOND-COURSE", R.string.type_of_dish_second_course),
            new Pair<>("MAIN-DISH", R.string.type_of_dish_main_dish),
            new Pair<>("DESSERT", R.string.type_of_dish_dessert),
            new Pair<>("OTHER", R.string.type_of_dish_other)
    ));


    // Difficulties data

    private static int mDefaultDifficultyPosition = 1;

    private static ArrayList<Pair<String, Integer>> mDifficulties = new ArrayList<>(Arrays.asList(
            new Pair<>("LOW", R.string.difficulty_low),
            new Pair<>("MEDIUM", R.string.difficulty_medium),
            new Pair<>("HIGH", R.string.difficulty_high)
    ));

    // Units data

    private static int mDefaultUnitPosition = 0;

    private static ArrayList<Pair<String, Integer>> mUnits = new ArrayList<>(Arrays.asList(
            new Pair<>("unit", R.string.measurement_unit_unit_plural),
            new Pair<>("g", R.string.measurement_unit_g_plural),
            new Pair<>("kg", R.string.measurement_unit_kg_plural),
            new Pair<>("ml", R.string.measurement_unit_ml_plural),
            new Pair<>("l", R.string.measurement_unit_l_plural),
            new Pair<>("cup", R.string.measurement_unit_cup_plural),
            new Pair<>("tsp", R.string.measurement_unit_tsp_plural),
            new Pair<>("tbsp", R.string.measurement_unit_tbsp_plural),
            new Pair<>("rasher", R.string.measurement_unit_rasher_plural)
    ));

    // CATEGORIES

    /**
     * Get all categproes strings
     *
     * @param c Context to get the strings
     * @return Categories strings array
     */
    public static ArrayList<String> getCategories(Context c) {
        ArrayList<String> categories = new ArrayList<>();

        for (int i = 0; i < mCategories.size(); i++) {
            categories.add(c.getString(mCategories.get(i).second));
        }

        return categories;
    }

    /**
     * Return the default category string
     *
     * @return default category string
     */
    public static String getDefaultCategory() {
        return mCategories.get(mDefaultCategoryPosition).first;
    }

    /**
     * Translate a position integer into a category code
     *
     * @param position API category code
     * @return user-friendly category string
     */
    public static String getCategoryTranslationByPosition(int position, Context c) {
        return c.getString(mCategories.get(position).second);
    }

    /**
     * Get category position by its code
     *
     * @param code API category code
     * @return position integer
     */
    public static int getCategoryPosition(String code) {
        int position = mDefaultCategoryPosition;

        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * Get category position by its code
     *
     * @param position API category code
     * @return position integer
     */
    public static String getCategoryCodeByPosition(int position) {
        return mCategories.get(position).first;
    }

    /**
     * Translate category code to an understandable string
     *
     * @param code API category code
     * @return user-friendly category string
     */
    public static String getCategoryTranslation(String code, Context c) {
        int position = mDefaultCategoryPosition;

        for (int i = 0; i < mTypes.size(); i++) {
            if (mCategories.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return c.getString(mCategories.get(position).second);
    }


    // TYPES OF DISH

    /**
     * Get all types strings
     *
     * @param c Context to get the strings
     * @return Types strings array
     */
    public static ArrayList<String> getTypes(Context c) {
        ArrayList<String> types = new ArrayList<>();

        for (int i = 0; i < mTypes.size(); i++) {
            types.add(c.getString(mTypes.get(i).second));
        }

        return types;
    }

    /**
     * Return the default type of dish string
     *
     * @return default type of dish string
     */
    public static String getDefaultTypeOfDish() {
        return mTypes.get(mDefaultTypePosition).first;
    }

    /**
     * Translate a position integer into a typeOfDish code
     *
     * @param position API type of dish code
     * @return user-friendly type of dish string
     */
    public static String getTypeOfDishTranslationByPosition(int position, Context c) {
        return c.getString(mTypes.get(position).second);
    }

    /**
     * Get typeOfDish position by its code
     *
     * @param code API type of dish code
     * @return position integer
     */
    public static int getTypeOfDishPosition(String code) {
        int position = mDefaultTypePosition;

        for (int i = 0; i < mTypes.size(); i++) {
            if (mTypes.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * Translate typeOfDish code to an understandable string
     *
     * @param code API type of dish code
     * @return user-friendly type of dish string
     */
    public static String getTypeOfDishTranslation(String code, Context c) {
        int position = mDefaultTypePosition;

        for (int i = 0; i < mTypes.size(); i++) {
            if (mTypes.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return c.getString(mTypes.get(position).second);
    }

    // DIFFICULTIES

    /**
     * Get all difficulties strings
     *
     * @param c Context to get the strings
     * @return Difficulties strings array
     */
    public static ArrayList<String> getDifficulties(Context c) {
        ArrayList<String> difficulties = new ArrayList<>();

        for (int i = 0; i < mDifficulties.size(); i++) {
            difficulties.add(c.getString(mDifficulties.get(i).second));
        }

        return difficulties;
    }

    /**
     * Return the default type of diffculty string
     *
     * @return default difficulty string
     */
    public static String getDefaultDifficulty() {
        return mDifficulties.get(mDefaultDifficultyPosition).first;
    }

    /**
     * Translate a position integer into a difficulty code
     *
     * @param position API difficulty code
     * @return user-friendly difficulty string
     */
    public static String getDifficultyTranslationByPosition(int position, Context c) {
        return c.getString(mDifficulties.get(position).second);
    }

    /**
     * Get difficulty position by its code
     *
     * @param code API difficulty code
     * @return position integer
     */
    public static int getDifficultyPosition(String code) {
        int position = mDefaultDifficultyPosition;

        for (int i = 0; i < mDifficulties.size(); i++) {
            if (mDifficulties.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * Translate difficulty code to an understandable string
     *
     * @param code API difficulty code
     * @return user-friendly difficulty string
     */
    public static String getDifficultyTranslation(String code, Context c) {
        int position = mDefaultDifficultyPosition;

        for (int i = 0; i < mDifficulties.size(); i++) {
            if (mDifficulties.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return c.getString(mDifficulties.get(position).second);
    }

    // MEASUREMENT UNITS

    /**
     * Get all measurement units strings
     *
     * @param c Context to get the strings
     * @return Units strings array
     */
    public static ArrayList<String> getMeasurementUnits(Context c) {
        ArrayList<String> units = new ArrayList<>();

        for (int i = 0; i < mUnits.size(); i++) {
            units.add(c.getString(mUnits.get(i).second));
        }

        return units;
    }

    /**
     * Return the default measuremente unit string
     *
     * @return default unit string
     */
    public static String getDefaultMeasurementUnit() {
        return mUnits.get(mDefaultUnitPosition).first;
    }

    /**
     * Translate a position integer into a unit code
     *
     * @param position API unit code
     * @return user-friendly unit string
     */
    public static String getMeasurementUnitTranslationByPosition(int position, Context c) {
        return c.getString(mUnits.get(position).second);
    }

    /**
     * Get unit position by its code
     *
     * @param code API unit code
     * @return position integer
     */
    public static int getMeasurementUnitPosition(String code) {
        int position = mDefaultUnitPosition;

        for (int i = 0; i < mUnits.size(); i++) {
            if (mUnits.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * Translate unit code to an understandable string
     *
     * @param code API unit code
     * @return user-friendly unit string
     */
    public static String getMeasurementUnitTranslation(String code, Context c) {
        int position = mDefaultUnitPosition;

        for (int i = 0; i < mUnits.size(); i++) {
            if (mUnits.get(i).first.equals(code)) {
                position = i;
                break;
            }
        }

        return c.getString(mUnits.get(position).second);
    }


    // INGREDIENTS' QUANTITY

    /**
     * Translate ingredient's quantity code to an understandable string
     *
     * @param quantity  Float quantity
     * @param unit_code Code of measurement unit
     * @return User-friendly string
     */
    public static String getIngredientQuantity(float quantity, String unit_code, Context c) {
        String q = "", u = "";
        boolean plural = true;

        if (quantity > 0) {
            float result = quantity - (int) quantity;
            if (result != 0) {
                q = String.format("%.2f", quantity) + " ";
            } else {
                q = String.format("%.0f", quantity) + " ";
            }

            if (quantity <= 1) {
                plural = false;
            }

            if (quantity == 0.25) {
                q = "1/4 ";
            }
            if (quantity == 0.5) {
                q = "1/2 ";
            }
            if (quantity == 0.75) {
                q = "3/4 ";
            }
        }

        if (!unit_code.equals("unit")) {
            switch (unit_code) {
                case "g":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_g_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_g) + " ";
                    }
                    break;
                case "kg":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_kg_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_kg) + " ";
                    }
                    break;
                case "ml":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_ml_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_ml) + " ";
                    }
                    break;
                case "l":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_l_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_l) + " ";
                    }
                    break;
                case "cup":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_cup_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_cup) + " ";
                    }
                    break;
                case "tsp":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_tsp_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_tsp) + " ";
                    }
                    break;
                case "tbsp":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_tbsp_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_tbsp) + " ";
                    }
                    break;
                case "rasher":
                    if (plural) {
                        u = c.getString(R.string.measurement_unit_rasher_plural) + " ";
                    } else {
                        u = c.getString(R.string.measurement_unit_rasher) + " ";
                    }
                    break;
                default:
                case "unit":
                    break;
            }
        }

        return q + u;
    }

    /**
     * Translate hours and minutes into a valid string
     *
     * @param cookingHours Number of hours
     * @param cookingMinutes Number of minutes
     * @param c Context
     * @return User-friendly string
     */
    public static String getCookingTimeLabel(Integer cookingHours, Integer cookingMinutes, Context c) {
        if (cookingHours > 0) {
            if (cookingMinutes > 0) {
                return cookingHours.toString() + " " +
                    ((cookingHours == 1) ? c.getString(R.string.detail_hour) : c.getString(R.string.detail_hours)) +
                    ", " + cookingMinutes.toString() + " " +
                    ((cookingMinutes == 1) ? c.getString(R.string.detail_minute) : c.getString(R.string.detail_minutes));
            } else {
                return cookingHours + " " +
                    ((cookingHours == 1) ? c.getString(R.string.detail_hour) : c.getString(R.string.detail_hours));
            }
        } else {
            return cookingMinutes + " " +
                ((cookingMinutes == 1) ? c.getString(R.string.detail_minute) : c.getString(R.string.detail_minutes));
        }
    }

    /**
     * Translate servings into a valid string
     *
     * @param servings Number of servings
     * @param c Context
     * @return User-friendly string
     */
    public static String getServingsLabel(Integer servings, Context c) {
        return servings.toString() + " " + ((servings == 1) ? c.getString(R.string.recipe_edition_serving) :
                c.getString(R.string.recipe_edition_servings));
    }

    /**
     * Translate users into a valid string
     *
     * @param users Number of users
     * @param c Context
     * @return User-friendly string
     */
    public static String getUsersLabel(Integer users, Context c) {
        if (users == 1) {
            return "(" + users.toString() + " " + c.getString(R.string.detail_user).toLowerCase() + ")";
        } else {
            return "(" + users.toString() + " " + c.getString(R.string.detail_users).toLowerCase() + ")";
        }
    }
}
