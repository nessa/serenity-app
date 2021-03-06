package com.amusebouche.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Recipe category class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain all recipe categories's data. Needed from recipe class.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class RecipeCategory implements Parcelable {

    // Category values
    public static String CATEGORY_ALLERGY_GLUTEN_CODE = "GLUTEN_ALLERGY";
    public static String CATEGORY_ALLERGY_LACTOSE_CODE = "LACTOSE_ALLERGY";
    public static String CATEGORY_ALLERGY_SHELLFISH_CODE = "SHELLFISH_ALLERGY";
    public static String CATEGORY_ALLERGY_FISH_CODE = "FISH_ALLERGY";
    public static String CATEGORY_ALLERGY_DRIED_FRUIT_CODE = "DRIED_FRUIT_ALLERGY";
    public static String CATEGORY_DIET_MEDITERRANEAN = "MEDITERRANEAN";
    public static String CATEGORY_DIET_VEGETARIAN = "VEGETARIAN";
    public static String CATEGORY_DIET_VEGAN = "VEGAN";
    public static String CATEGORY_UNCATEGORIZED = "UNCATEGORIZED";

    // Manual categories
    public static ArrayList<String> MANUAL_CATEGORIES = new ArrayList<>(Arrays.asList(
            CATEGORY_DIET_MEDITERRANEAN,
            CATEGORY_DIET_VEGETARIAN,
            CATEGORY_DIET_VEGAN
    ));

    // Main variables

    @Expose
    @SerializedName("name")
    private String mName;

    // Constructors

    /**
     * Basic category constructor
     * @param name Category name
     */
    public RecipeCategory(String name) {
        this.mName = name;
    }

    /**
     * Special contructor
     * @param o JSONObject that contains all category information
     */
    public RecipeCategory(JSONObject o) {
        try {
            this.mName = o.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parcelable constructor
     * @param source Parcel source data
     */
    public RecipeCategory(Parcel source){
        this.mName = source.readString();
    }

    // Getters

    /**
     * Get method for name variable
     * @return Recipe name
     */
    public String getName() {
        return mName;
    }

    // JSON methods

    /**
     * Build a JSON object with the category data
     *
     * @return o JSONObject that contains all category information
     */
    public JSONObject buildJSON() {

        JSONObject json = new JSONObject();

        try {
            json.put("name", this.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    // Parcelable methods

    /**
     * Check if this category is different from a given one
     * @param c The given recipe category
     * @return True if the category is different. Otherwise, false.
     */
    public Boolean diff(RecipeCategory c) {
        return !mName.equals(c.getName());
    }

    /**
     * Method used to give additional hints on how to process the received parcel.
     * @return 0
     */
    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * Output to parcelable data
     * @param dest Parcelable data to fill with recipe category information
     * @param flags ...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
    }

    /**
     * Needed to complete parcelable configuration
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<RecipeCategory>() {
        public RecipeCategory createFromParcel(Parcel source) {
            return new RecipeCategory(source);
        }

        public RecipeCategory[] newArray(int size) {
            return new RecipeCategory[size];
        }
    };
}
