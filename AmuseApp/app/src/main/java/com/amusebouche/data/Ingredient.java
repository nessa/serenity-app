package com.amusebouche.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ingredient class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain ingredients's data and pass it through the activities.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class Ingredient implements Parcelable {

    public static String CATEGORY_SEPARATOR = "+";

    // API variables
    private String mTranslation;
    private String mLanguage;
    private Date mTimestamp;
    private String mCategories;

    // Database variables
    private String mDatabaseId;

    // Constructors

    /**
     * Basic ingredient contructor
     *
     * @param translation Ingredient translation
     * @param language Language in which the ingredient is translated
     * @param timestamp Recipe date of creation
     * @param categories String with the categories of the ingredient separated by "+"
     */
    public Ingredient(String translation, String language, Date timestamp, String categories) {
        this.mDatabaseId = "";
        this.mTranslation = translation;
        this.mLanguage = language;
        this.mTimestamp = timestamp;
        this.mCategories = categories;
    }

    /**
     * Basic ingredient contructor
     *
     * @param databaseId Ingredient identifier
     * @param translation Ingredient translation
     * @param language Language in which the ingredient is translated
     * @param timestamp Recipe date of creation
     * @param categories String with the categories of the ingredient separated by "+"
     */
    public Ingredient(String databaseId, String translation, String language, Date timestamp,
                      String categories) {
        this.mDatabaseId = databaseId;
        this.mTranslation = translation;
        this.mLanguage = language;
        this.mTimestamp = timestamp;
        this.mCategories = categories;
    }

    /**
     * Special contructor
     *
     * @param o JSONObject that contains all ingredient information as it comes from the API
     */
    public Ingredient(JSONObject o) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        try {
            this.mDatabaseId = "";
            this.mTranslation = o.getString("translation");
            this.mLanguage = o.getString("language");
            this.mTimestamp = format.parse(o.getString("timestamp"));

            JSONArray categories = o.getJSONObject("ingredient").getJSONArray("categories");
            this.mCategories = categories.join(CATEGORY_SEPARATOR);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parcelable constructor
     *
     * Every data MUST be read in the same order as was written in writeToParcel
     *
     * @param source Parcel source data
     */
    public Ingredient(Parcel source){
        this.mDatabaseId = source.readString();
        this.mTranslation = source.readString();
        this.mLanguage = source.readString();
        this.mTimestamp = new Date(source.readLong());
        this.mCategories = source.readString();
    }

    // Getters

    /**
     * Get method for database identifier
     * @return Ingredient database identifier
     */
    public String getDatabaseId() {
        return mDatabaseId;
    }

    /**
     * Get method for translation variable
     * @return Ingredient translation
     */
    public String getTranslation() {
        return mTranslation;
    }

    /**
     * Get method for language variable
     * @return Ingredient language
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * Get method for timestamp variable
     * @return Ingredient date of update
     */
    public Date getTimestamp() {
        return mTimestamp;
    }

    /**
     * Get method for categories variable
     * @return Ingredient categories
     */

    public String getCategories() {
        return mCategories;
    }

    // Setters

    /**
     * Set method for database identifier
     * @param databaseId Ingredient database identifier
     */
    public void setDatabaseId(String databaseId) {
        mDatabaseId = databaseId;
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
     *
     * Every data MUST be written  in the same order as was read in
     * the parcelable constructor
     *
     * @param dest Parcelable data to fill with ingredient information
     * @param flags ...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDatabaseId);
        dest.writeString(this.mTranslation);
        dest.writeString(this.mLanguage);
        dest.writeLong(this.mTimestamp.getTime());
        dest.writeString(this.mCategories);
    }

    /**
     * Needed to complete parcelable configuration
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
