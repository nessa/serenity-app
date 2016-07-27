package com.amusebouche.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    // Types of dish values
    public static String TYPE_OF_DISH_APPETIZER = "APPETIZER";
    public static String TYPE_OF_DISH_FIRST_COURSE = "FIRST-COURSE";
    public static String TYPE_OF_DISH_SECOND_COURSE = "SECOND-COURSE";
    public static String TYPE_OF_DISH_MAIN_DISH = "MAIN-DISH";
    public static String TYPE_OF_DISH_DESSERT = "DESSERT";
    public static String TYPE_OF_DISH_OTHER = "OTHER";

    // Difficulties values
    public static String DIFFICULTY_LOW = "LOW";
    public static String DIFFICULTY_MEDIUM = "MEDIUM";
    public static String DIFFICULTY_HIGH = "HIGH";

    // API variables
    private transient String mId;

    @Expose
    @SerializedName("title")
    private String mTitle;

    private transient String mOwner;

    @Expose
    @SerializedName("language")
    private String mLanguage;

    @Expose
    @SerializedName("type_of_dish")
    private String mTypeOfDish;

    @Expose
    @SerializedName("difficulty")
    private String mDifficulty;

    private transient Date mCreatedTimestamp;
    private transient Date mUpdatedTimestamp;

    @Expose
    @SerializedName("cooking_time")
    private Float mCookingTime;

    @Expose
    @SerializedName("image")
    private String mImage;

    private transient Integer mTotalRating;
    private transient Integer mUsersRating;

    @Expose
    @SerializedName("servings")
    private Integer mServings;

    @Expose
    @SerializedName("source")
    private String mSource;

    // Database variables
    private transient String mDatabaseId;
    private transient String mLocalImage;

    // Local variables
    private transient Boolean mIsOnline;

    // List variables

    @Expose
    @SerializedName("categories")
    private ArrayList<RecipeCategory> mCategories;

    @Expose
    @SerializedName("ingredients")
    private ArrayList<RecipeIngredient> mIngredients;

    @Expose
    @SerializedName("directions")
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

        // Default values
        this.mLocalImage = "";
        this.mIsOnline = false;
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

        // Default values
        this.mLocalImage = "";
        this.mIsOnline = false;
    }


    /**
     * Special contructor
     *
     * @param o JSONObject that contains all recipe information
     */
    public Recipe(JSONObject o) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

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

            // Default values
            this.mLocalImage = "";
            this.mIsOnline = false;

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
     *
     * Every data MUST be read in the same order as was written in writeToParcel
     *
     * @param source Parcel source data
     */
    public Recipe(Parcel source){
        this.mDatabaseId = source.readString();
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
        this.mLocalImage = source.readString();
        this.mTotalRating = source.readInt();
        this.mUsersRating = source.readInt();
        this.mServings = source.readInt();
        this.mSource = source.readString();
        this.mIsOnline = source.readByte() != 0;

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
     * Get method for local image variable
     * @return URL/path to the recipe main image in local storage
     */
    public String getLocalImage() {
        return mLocalImage;
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
     * Get method for isOnline variable
     * @return boolean If true, recipe is online
     * Else, recipe is local
     */
    public boolean getIsOnline() {
        return mIsOnline;
    }

    /**
     * Get method for categories variable
     * @return List of recipe categories
     */
    public ArrayList<RecipeCategory> getCategories() {
        return mCategories;
    }

    /**
     * Get method for ingredients variable
     * @return List of recipe ingredients
     */
    public ArrayList<RecipeIngredient> getIngredients() {
        return mIngredients;
    }

    /**
     * Get method for directions variable
     * @return List of recipe directions
     */
    public ArrayList<RecipeDirection> getDirections() {
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
     * Set method for id variable
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Set method for title variable
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Set method for owner variable
     */
    public void setOwner(String owner) {
        this.mOwner = owner;
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

    /**
     * Set method for local image variable
     */
    public void setLocalImage(String image) {
        this.mLocalImage = image;
    }

    /**
     * Set method for isOnline variable
     */
    public void setIsOnline(boolean isOnline) {
        this.mIsOnline = isOnline;
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
        dest.writeString(this.mDatabaseId);
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mOwner);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mTypeOfDish);
        dest.writeString(this.mDifficulty);
        dest.writeLong(this.mCreatedTimestamp == null ? 0 : this.mCreatedTimestamp.getTime());
        dest.writeLong(this.mUpdatedTimestamp == null ? 0 : this.mUpdatedTimestamp.getTime());
        dest.writeFloat(this.mCookingTime);
        dest.writeString(this.mImage);
        dest.writeString(this.mLocalImage);
        dest.writeInt(this.mTotalRating);
        dest.writeInt(this.mUsersRating);
        dest.writeInt(this.mServings);
        dest.writeString(this.mSource);
        dest.writeByte((byte) (this.mIsOnline ? 1 : 0));

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
