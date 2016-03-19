package com.amusebouche.data;

import android.content.Context;
import android.util.Pair;

import com.amusebouche.amuseapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User friendly translator for recipes data.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 */
public class UserFriendlyRecipeData {

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
}
