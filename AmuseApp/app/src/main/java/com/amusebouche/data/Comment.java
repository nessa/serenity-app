package com.amusebouche.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Comment class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain comment's data and pass it through the activities.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class Comment implements Parcelable {

    // API variables
    private String mId;
    private String mRecipeId;
    private String mUser;
    private String mComment;
    private Date mTimestamp;

    // Constructors

    /**
     * Basic recipe contructor
     *
     * @param id API recipe identifier
     * @param recipeId Recipe id
     * @param user User that wrote the comment
     * @param comment Content of the comment
     * @param timestamp Recipe date of creation
     */
    public Comment(String id, String recipeId, String user, String comment, Date timestamp) {
        this.mId = id;
        this.mRecipeId = recipeId;
        this.mUser = user;
        this.mComment = comment;
        this.mTimestamp = timestamp;
    }


    /**
     * Special contructor
     *
     * @param o JSONObject that contains all comment information
     */
    public Comment(JSONObject o) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        Log.d("JSON", o.toString());

        try {
            if (o.has("id")) {
                this.mId = o.getString("id");
            } else {
                this.mId = "0";
            }

            if (o.has("timestamp")) {
                this.mTimestamp = format.parse(o.getString("timestamp"));
            } else {
                this.mTimestamp = new Date();
            }

            this.mRecipeId = o.getString("recipe");
            this.mUser = o.getString("user");
            this.mComment = o.getString("comment");
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
    public Comment(Parcel source){
        this.mId = source.readString();
        this.mRecipeId = source.readString();
        this.mUser = source.readString();
        this.mComment = source.readString();
        this.mTimestamp = new Date(source.readLong());
    }

    // Getters

    /**
     * Get method for id variable
     * @return Comment identifier
     */
    public String getId() {
        return mId;
    }

    /**
     * Get method for user variable
     * @return Comment user
     */
    public String getUser() {
        return mUser;
    }

    /**
     * Get method for comment variable
     * @return Comment content
     */
    public String getComment() {
        return mComment;
    }

    /**
     * Get method for timestamp variable
     * @return Comment date of creation
     */
    public Date getTimestamp() {
        return mTimestamp;
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
     * @param dest Parcelable data to fill with recipe information
     * @param flags ...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mRecipeId);
        dest.writeString(this.mUser);
        dest.writeString(this.mComment);
        dest.writeLong(this.mTimestamp.getTime());
    }

    /**
     * Needed to complete parcelable configuration
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

}
