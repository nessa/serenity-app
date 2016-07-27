package com.amusebouche.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Recipe ingredient class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain all recipe ingredient's data. Needed from recipe class.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class RecipeIngredient implements Parcelable {

    // Main variables

    @Expose
    @SerializedName("sort_number")
    private Integer mSortNumber;

    @Expose
    @SerializedName("name")
    private String mName;

    @Expose
    @SerializedName("quantity")
    private Float mQuantity;

    @Expose
    @SerializedName("measurement_unit")
    private String mMeasurementUnit;


    // Constructors

    /**
     * Basic ingredient constructor
     * @param sortNumber Ingredient sort number
     * @param name Ingredient name
     * @param quantity Ingredient quantity
     * @param measurementUnit Ingredient measurement unit
     */
    public RecipeIngredient(Integer sortNumber, String name, Float quantity,
                      String measurementUnit) {
        this.mSortNumber = sortNumber;
        this.mName = name;
        this.mQuantity = quantity;
        this.mMeasurementUnit = measurementUnit;
    }

    /**
     * Special contructor
     * @param o JSONObject that contains all ingredient information
     */
    public RecipeIngredient(JSONObject o) {
        try {
            this.mSortNumber = o.getInt("sort_number");
            this.mName = o.getString("name");
            this.mQuantity = Float.valueOf(o.getString("quantity"));
            this.mMeasurementUnit = o.getString("measurement_unit");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parcelable constructor
     * @param source Parcel source data
     */
    public RecipeIngredient(Parcel source){
        this.mSortNumber = source.readInt();
        this.mName = source.readString();
        this.mQuantity = source.readFloat();
        this.mMeasurementUnit = source.readString();
    }

    // Getters

    /**
     * Get method for sortNumber variable
     * @return Ingredient sort number
     */
    public Integer getSortNumber() {
        return mSortNumber;
    }

    /**
     * Get method for name variable
     * @return Ingredient name
     */
    public String getName() {
        return mName;
    }

    /**
     * Get method for quantity variable
     * @return Ingredient quantity
     */
    public Float getQuantity() {
        return mQuantity;
    }

    /**
     * Get method for measurementUnit variable
     * @return Ingredient measurement unit
     */
    public String getMeasurementUnit() {
        return mMeasurementUnit;
    }


    // Setters

    /**
     * Set method for sortNumber variable
     */
    public void setSortNumber(Integer sortNumber) {
        this.mSortNumber = sortNumber;
    }

    /**
     * Set method for name variable
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Set method for quantity variable
     */
    public void setQuantity(Float quantity) {
        this.mQuantity = quantity;
    }

    /**
     * Set method for measurementUnit variable
     */
    public void setMeasurementUnit(String measurementUnit) {
        this.mMeasurementUnit = measurementUnit;
    }

    // JSON methods

    /**
     * Build a JSON object with the ingredient data
     *
     * @return o JSONObject that contains all ingredient information
     */
    public JSONObject buildJSON() {

        JSONObject json = new JSONObject();

        try {
            json.put("sort_number", this.getSortNumber());
            json.put("name", this.getName());
            json.put("quantity", this.getQuantity());
            json.put("measurement_unit", this.getMeasurementUnit());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
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
     * @param dest Parcelable data to fill with recipe ingredient information
     * @param flags ...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSortNumber);
        dest.writeString(this.mName);
        dest.writeFloat(this.mQuantity);
        dest.writeString(this.mMeasurementUnit);
    }

    /**
     * Needed to complete parcelable configuration
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<RecipeIngredient>() {
        public RecipeIngredient createFromParcel(Parcel source) {
            return new RecipeIngredient(source);
        }

        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }
    };

}