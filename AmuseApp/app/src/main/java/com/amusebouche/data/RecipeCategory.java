package com.amusebouche.data;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Recipe category class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain all recipe categories's data. Needed from recipe class.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class RecipeCategory implements Parcelable {

    // Main variables
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


    // Parcelable methods

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
