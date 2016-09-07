package com.amusebouche.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Recipe direction class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain all recipe directions's data. Needed from recipe class.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class RecipeDirection implements Parcelable {

    // Main variables

    @Expose
    @SerializedName("sort_number")
    private Integer mSortNumber;

    @Expose
    @SerializedName("description")
    private String mDescription;

    @Expose
    @SerializedName("image")
    private String mImage;

    @Expose
    @SerializedName("video")
    private String mVideo;

    @Expose
    @SerializedName("time")
    private Float mTime;

    // Constructors

    /**
     * Basic category constructor
     * @param sortNumber Direction sort number
     * @param description Direction description
     * @param image Direction image
     * @param video Direction video
     * @param time Direction time
     */
    public RecipeDirection(Integer sortNumber, String description, String image, String video,
                     Float time) {
        this.mSortNumber = sortNumber;
        this.mDescription = description;
        this.mImage = image;
        this.mVideo = video;
        this.mTime = time;
    }

    /**
     * Special contructor
     * @param o JSONObject that contains all direction information
     */
    public RecipeDirection(JSONObject o) {
        try {
            this.mSortNumber = o.getInt("sort_number");
            this.mDescription = o.getString("description");
            this.mImage = o.getString("image");
            this.mVideo = o.getString("video");
            this.mTime = Float.valueOf(o.getString("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parcelable constructor
     * @param source Parcel source data
     */
    public RecipeDirection(Parcel source){
        this.mSortNumber = source.readInt();
        this.mDescription = source.readString();
        this.mImage = source.readString();
        this.mVideo = source.readString();
        this.mTime = source.readFloat();
    }

    // Getters

    /**
     * Get method for sortNumber variable
     * @return Direction sort number
     */
    public Integer getSortNumber() {
        return mSortNumber;
    }

    /**
     * Get method for description variable
     * @return Direction description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Get method for image variable
     * @return Direction image
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Get method for video variable
     * @return Direction video
     */
    public String getVideo() {
        return mVideo;
    }

    /**
     * Get method for time variable
     * @return Direction time
     */
    public Float getTime() {
        return mTime;
    }

    // Setters

    /**
     * Set method for sortNumber variable
     * @param sortNumber sort number to set
     */
    public void setSortNumber(Integer sortNumber) {
        mSortNumber = sortNumber;
    }

    /**
     * Set method for description variable
     * @param description description to set
     */
    public void setDescription(String description) {
        mDescription = description;
    }

    /**
     * Set method for image variable
     * @param image image to set
     */
    public void setImage(String image) {
        mImage = image;
    }

    /**
     * Set method for video variable
     * @param video video to set
     */
    public void setVideo(String video) {
        mVideo = video;
    }

    /**
     * Get method for time variable
     * @param time time to set
     */
    public void setTime(Float time) {
        mTime = time;
    }

    // JSON methods

    /**
     * Build a JSON object with the direction data
     *
     * @return o JSONObject that contains all direction information
     */
    public JSONObject buildJSON() {

        JSONObject json = new JSONObject();

        try {
            json.put("sort_number", this.getSortNumber());
            json.put("description", this.getDescription());
            json.put("image", this.getImage());
            json.put("video", this.getVideo());
            json.put("time", this.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


    // Parcelable methods

    /**
     * Check if this direction is different from a given one
     * @param d The given recipe direction
     * @return True if the direction is different. Otherwise, false.
     */
    public Boolean diff(RecipeDirection d) {
        boolean equals = mSortNumber.equals(d.getSortNumber());
        equals = equals && mDescription.equals(d.getDescription());
        equals = equals && mImage.equals(d.getImage());
        equals = equals && mVideo.equals(d.getVideo());
        equals = equals && mTime.equals(d.getTime());
        return !equals;
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
     * @param dest Parcelable data to fill with recipe direction information
     * @param flags ...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSortNumber);
        dest.writeString(this.mDescription);
        dest.writeString(this.mImage);
        dest.writeString(this.mVideo);
        dest.writeFloat(this.mTime);
    }

    /**
     * Needed to complete parcelable configuration
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<RecipeDirection>() {
        public RecipeDirection createFromParcel(Parcel source) {
            return new RecipeDirection(source);
        }

        public RecipeDirection[] newArray(int size) {
            return new RecipeDirection[size];
        }
    };
}

