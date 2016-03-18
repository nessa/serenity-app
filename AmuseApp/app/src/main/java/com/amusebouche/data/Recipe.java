package com.amusebouche.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Recipe class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to contain all recipe's data and pass it through the activities.
 * @implements Parcelable. Needed to pass this class through intents and bundles.
 */
public class Recipe implements Parcelable {

    // Main variables
    private String mDatabaseId;
    private String mId;
    private String mTitle;
    private String mOwner;
    private String mLanguage;
    private String mTypeOfDish;
    private String mDifficulty;
    private Date mCreatedTimestamp;
    private Date mUpdatedTimestamp;
    private Float mCookingTime;
    private String mImage;
    private Integer mTotalRating;
    private Integer mUsersRating;
    private Integer mServings;
    private String mSource;

    // List variables
    private ArrayList<RecipeCategory> mCategories;
    private ArrayList<RecipeIngredient> mIngredients;
    private ArrayList<RecipeDirection> mDirections;

    // Constructors

    /**
     * Basic recipe contructor
     *
     * @param databaseId Database recipe identifier
     * @param id API recipe identifier
     * @param title Recipe title
     * @param owner User that owns this recipe
     * @param language Recipe language (EN, ES, ...)
     * @param typeOfDish Recipe dish (DESSERT, MAIN DISH, ...)
     * @param difficulty Recipe difficulty (HIGH, MEDIUM, HARD)
     * @param createdTimestamp Recipe date of creation
     * @param updatedTimestamp Recipe date of update
     * @param cookingTime Time to cook this recipe
     * @param image URL/path to the main image
     * @param totalRating Total rating value for this recipe
     * @param usersRating Number of users that have rated this recipe
     * @param servings Number of persons we can serve this recipe (with the given amount of
     *                 ingredients)
     * @param source URL to the source of this recipe
     * @param categories List of categories associated to this recipe. @see Category clas
     * @param ingredients List of ingredients associated to this recipe. @see Ingredient class
     * @param directions List of directions associated to this recipe. @see Direction class
     */
    public Recipe(String databaseId, String id, String title, String owner, String language,
                  String typeOfDish, String difficulty, Date createdTimestamp,
                  Date updatedTimestamp, Float cookingTime, String image, Integer totalRating,
                  Integer usersRating, Integer servings, String source,
                  ArrayList<RecipeCategory> categories, ArrayList<RecipeIngredient> ingredients,
                  ArrayList<RecipeDirection> directions) {
        this.mDatabaseId = databaseId;
        this.mId = id;
        this.mTitle = title;
        this.mOwner = owner;
        this.mLanguage = language;
        this.mTypeOfDish = typeOfDish;
        this.mDifficulty = difficulty;
        this.mCreatedTimestamp = createdTimestamp;
        this.mUpdatedTimestamp = updatedTimestamp;
        this.mCookingTime = cookingTime;
        this.mImage = image;
        this.mTotalRating = totalRating;
        this.mUsersRating = usersRating;
        this.mServings = servings;
        this.mSource = source;
        this.mCategories = categories;
        this.mIngredients = ingredients;
        this.mDirections = directions;
    }

    /**
     * Basic recipe contructor without database id
     *
     * @param id Recipe identifier
     * @param title Recipe title
     * @param owner User that owns this recipe
     * @param language Recipe language (EN, ES, ...)
     * @param typeOfDish Recipe dish (DESSERT, MAIN DISH, ...)
     * @param difficulty Recipe difficulty (HIGH, MEDIUM, HARD)
     * @param createdTimestamp Recipe date of creation
     * @param updatedTimestamp Recipe date of update
     * @param cookingTime Time to cook this recipe
     * @param image URL/path to the main image
     * @param totalRating Total rating value for this recipe
     * @param usersRating Number of users that have rated this recipe
     * @param servings Number of persons we can serve this recipe (with the given amount of
     *                 ingredients)
     * @param source URL to the source of this recipe
     * @param categories List of categories associated to this recipe. @see Category clas
     * @param ingredients List of ingredients associated to this recipe. @see Ingredient class
     * @param directions List of directions associated to this recipe. @see Direction class
     */
    public Recipe(String id, String title, String owner, String language, String typeOfDish,
                  String difficulty, Date createdTimestamp, Date updatedTimestamp,
                  Float cookingTime, String image, Integer totalRating, Integer usersRating,
                  Integer servings, String source, ArrayList<RecipeCategory> categories,
                  ArrayList<RecipeIngredient> ingredients, ArrayList<RecipeDirection> directions) {
        this.mDatabaseId = "";
        this.mId = id;
        this.mTitle = title;
        this.mOwner = owner;
        this.mLanguage = language;
        this.mTypeOfDish = typeOfDish;
        this.mDifficulty = difficulty;
        this.mCreatedTimestamp = createdTimestamp;
        this.mUpdatedTimestamp = updatedTimestamp;
        this.mCookingTime = cookingTime;
        this.mImage = image;
        this.mTotalRating = totalRating;
        this.mUsersRating = usersRating;
        this.mServings = servings;
        this.mSource = source;
        this.mCategories = categories;
        this.mIngredients = ingredients;
        this.mDirections = directions;
    }


    /**
     * Special contructor
     *
     * @param o JSONObject that contains all recipe information
     */
    public Recipe(JSONObject o) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

        Log.d("JSON", o.toString());

        try {
            if (o.has("id")) {
                this.mId = o.getString("id");
            } else {
                this.mId = "0";
            }

            if (o.has("created_timestamp")) {
                this.mCreatedTimestamp = format.parse(o.getString("created_timestamp"));
            } else {
                this.mCreatedTimestamp = new Date();
            }

            if (o.has("updated_timestamp")) {
                this.mUpdatedTimestamp = format.parse(o.getString("updated_timestamp"));
            } else {
                this.mUpdatedTimestamp = new Date();
            }

            this.mDatabaseId = "";
            this.mTitle = o.getString("title");
            this.mOwner = o.getString("owner");
            this.mLanguage = o.getString("language");
            this.mTypeOfDish = o.getString("type_of_dish");
            this.mDifficulty = o.getString("difficulty");
            this.mCookingTime = Float.parseFloat(o.getString("cooking_time"));
            this.mImage = o.getString("image");
            this.mTotalRating = o.getInt("total_rating");
            this.mUsersRating = o.getInt("users_rating");
            this.mServings = o.getInt("servings");
            this.mSource = o.getString("source");

            // Create empty arrays for these variables
            mCategories = new ArrayList<>();
            mIngredients = new ArrayList<>();
            mDirections = new ArrayList<>();

            // Insert every element by constructing them from the JSON objects
            JSONArray categories = o.getJSONArray("categories");
            JSONArray ingredients = o.getJSONArray("ingredients");
            JSONArray directions = o.getJSONArray("directions");

            for (int i = 0; i < categories.length(); i++) {
                mCategories.add(new RecipeCategory(categories.getJSONObject(i)));
            }
            for (int i = 0; i < ingredients.length(); i++) {
                mIngredients.add(new RecipeIngredient(ingredients.getJSONObject(i)));
            }
            for (int i = 0; i < directions.length(); i++) {
                mDirections.add(new RecipeDirection(directions.getJSONObject(i)));
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parcelable constructor
     * @param source Parcel source data
     */
    public Recipe(Parcel source){
        this.mDatabaseId = "";
        this.mId = source.readString();
        this.mTitle = source.readString();
        this.mOwner = source.readString();
        this.mLanguage = source.readString();
        this.mTypeOfDish = source.readString();
        this.mDifficulty = source.readString();
        this.mCreatedTimestamp = new Date(source.readLong());
        this.mUpdatedTimestamp = new Date(source.readLong());
        this.mCookingTime = source.readFloat();
        this.mImage = source.readString();
        this.mTotalRating = source.readInt();
        this.mUsersRating = source.readInt();
        this.mServings = source.readInt();
        this.mSource = source.readString();

        mCategories = new ArrayList<>();
        mIngredients = new ArrayList<>();
        mDirections = new ArrayList<>();
        source.readList(this.mCategories, RecipeCategory.class.getClassLoader());
        source.readList(this.mIngredients, RecipeIngredient.class.getClassLoader());
        source.readList(this.mDirections, RecipeDirection.class.getClassLoader());
    }

    // Getters

    /**
     * Get method for id variable
     * @return Recipe identifier
     */
    public String getDatabaseId() {
        return mDatabaseId;
    }

    /**
     * Get method for id variable
     * @return Recipe identifier
     */
    public String getId() {
        return mId;
    }

    /**
     * Get method for title variable
     * @return Recipe title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get method for owner variable
     * @return Recipe owner
     */
    public String getOwner() {
        return mOwner;
    }

    /**
     * Get method for language variable
     * @return Recipe language
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * Get method for typeOfDish variable
     * @return Recipe type of dish
     */
    public String getTypeOfDish() {
        return mTypeOfDish;
    }

    /**
     * Get method for difficulty variable
     * @return Recipe difficulty
     */
    public String getDifficulty() {
        return mDifficulty;
    }

    /**
     * Get method for createdTimestamp variable
     * @return Recipe date of creation
     */
    public Date getCreatedTimestamp() {
        return mCreatedTimestamp;
    }

    /**
     * Get method for updatedTimestamp variable
     * @return Recipe date of update
     */
    public Date getUpdatedTimestamp() {
        return mUpdatedTimestamp;
    }

    /**
     * Get method for cookingTime variable
     * @return Recipe cooking time
     */
    public Float getCookingTime() {
        return mCookingTime;
    }

    /**
     * Get method for image variable
     * @return URL/path to the recipe main image
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Get method fot totalRating variable
     * @return Total rating
     */
    public Integer getTotalRating() {
        return mTotalRating;
    }

    /**
     * Get method for usersRating variable
     * @return Number of users that have rated this recipe
     */
    public Integer getUsersRating() {
        return mUsersRating;
    }

    /**
     * Get method for servings variable
     * @return Recipe servings
     */
    public Integer getServings() {
        return mServings;
    }

    /**
     * Get method for source variable
     * @return URL to the source of this recipe
     */
    public String getSource() {
        return mSource;
    }

    /**
     * Get method for categories variable
     * @return List of recipe categories
     */
    public ArrayList getCategories() {
        return mCategories;
    }

    /**
     * Get method for ingredients variable
     * @return List of recipe ingredients
     */
    public ArrayList getIngredients() {
        return mIngredients;
    }

    /**
     * Get method for directions variable
     * @return List of recipe directions
     */
    public ArrayList getDirections() {
        return mDirections;
    }

    // SETTERS

    /**
     * Set method for id variable
     */
    public void setDatabaseId(String databaseId) {
        this.mDatabaseId = databaseId;
    }

    /**
     * Set method for title variable
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Set method for typeOfDish variable
     */
    public void setTypeOfDish(String typeOfDish) {
        this.mTypeOfDish = typeOfDish;
    }

    /**
     * Set method for difficulty variable
     */
    public void setDifficulty(String difficulty) {
        this.mDifficulty = difficulty;
    }


    /**
     * Set method for cookingTime variable
     */
    public void setCookingTime(Float cookingTime) {
        this.mCookingTime = cookingTime;
    }

    /**
     * Set method for servings variable
     */
    public void setServings(Integer servings) {
        this.mServings = servings;
    }

    /**
     * Get method for source variable
     */
    public void setSource(String source) {
        this.mSource = source;
    }

    /**
     * Set method for image variable
     */
    public void setImage(String image) {
        this.mImage = image;
    }


    public void printString() {
        Log.d("RECIPE", mId + " " + mTitle);
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
     * @param dest Parcelable data to fill with recipe information
     * @param flags ...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mOwner);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mTypeOfDish);
        dest.writeString(this.mDifficulty);
        dest.writeLong(this.mCreatedTimestamp.getTime());
        dest.writeLong(this.mUpdatedTimestamp.getTime());
        dest.writeFloat(this.mCookingTime);
        dest.writeString(this.mImage);
        dest.writeInt(this.mTotalRating);
        dest.writeInt(this.mUsersRating);
        dest.writeInt(this.mServings);
        dest.writeString(this.mSource);

        dest.writeList(mCategories);
        dest.writeList(mIngredients);
        dest.writeList(mDirections);
    }

    /**
     * Needed to complete parcelable configuration
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

}
