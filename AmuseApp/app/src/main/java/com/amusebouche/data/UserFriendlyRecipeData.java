package com.amusebouche.data;

import com.amusebouche.amuseapp.R;

/**
 * User friendly translator for recipes data.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 */
public class UserFriendlyRecipeData {

    public static String getDefaultTypeOfDish() {
        return "FIRST-COURSE";
    }

    public static String getDefaultDifficulty() {
        return "LOW";
    }

    /**
     * Translate typeOfDish code to an understandable string
     *
     * @param code API type of dish code
     * @return integer code of string -> call to getString(int)
     */
    public static int getTypeOfDish(String code) {
        switch (code) {
            case "APPETIZER":
                return R.string.type_of_dish_appetizer;
            case "FIRST-COURSE":
                return R.string.type_of_dish_first_course;
            case "SECOND-COURSE":
                return R.string.type_of_dish_second_course;
            case "MAIN-DISH":
                return R.string.type_of_dish_main_dish;
            case "DESSERT":
                return R.string.type_of_dish_dessert;
            default:
            case "OTHER":
                return R.string.type_of_dish_other;
        }
    }


    /**
     * Translate difficulty code to an understandable string
     *
     * @param code API difficulty code
     * @return integer code of string -> call to getString(int)
     */
    public static int getDifficulty(String code) {
        switch (code) {
            case "HIGH":
                return R.string.difficulty_high;
            case "LOW":
                return R.string.difficulty_low;
            default:
            case "MEDIUM":
                return R.string.difficulty_medium;
        }
    }

}
