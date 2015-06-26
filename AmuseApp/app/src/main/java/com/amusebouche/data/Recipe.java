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
    private ArrayList<Category> mCategories;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Direction> mDirections;

    // Constructors

    /**
     * Basic recipe contructor
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
                  Integer servings, String source, ArrayList<Category> categories,
                  ArrayList<Ingredient> ingredients, ArrayList<Direction> directions) {
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

        try {
            this.mId = o.getString("id");
            this.mTitle = o.getString("title");
            this.mOwner = o.getString("owner");
            this.mLanguage = o.getString("language");
            this.mTypeOfDish = o.getString("type_of_dish");
            this.mDifficulty = o.getString("difficulty");
            this.mCreatedTimestamp = format.parse(o.getString("created_timestamp"));
            this.mUpdatedTimestamp = format.parse(o.getString("updated_timestamp"));
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
                mCategories.add(new Category(categories.getJSONObject(i)));
            }
            for (int i = 0; i < ingredients.length(); i++) {
                mIngredients.add(new Ingredient(ingredients.getJSONObject(i)));
            }
            for (int i = 0; i < directions.length(); i++) {
                mDirections.add(new Direction(directions.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parcelable constructor
     * @param source Parcel source data
     */
    public Recipe(Parcel source){
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
        source.readList(this.mCategories, Category.class.getClassLoader());
        source.readList(this.mIngredients, Ingredient.class.getClassLoader());
        source.readList(this.mDirections, Direction.class.getClassLoader());
    }

    // Getters

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
    public final Parcelable.Creator CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    /**
     * Recipe category class.
     * Author: Noelia Sales <noelia.salesmontes@gmail.com
     *
     * Class to contain all recipe categories's data. Needed from recipe class.
     * @implements Parcelable. Needed to pass this class through intents and bundles.
     */
    public class Category implements Parcelable {

        // Main variables
        private String mName;

        // Constructors

        /**
         * Basic category constructor
         * @param name Category name
         */
        public Category(String name) {
            this.mName = name;
        }

        /**
         * Special contructor
         * @param o JSONObject that contains all category information
         */
        public Category(JSONObject o) {
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
        public Category(Parcel source){
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
        public final Parcelable.Creator CREATOR = new Parcelable.Creator<Category>() {
            public Category createFromParcel(Parcel source) {
                return new Category(source);
            }

            public Category[] newArray(int size) {
                return new Category[size];
            }
        };
    }

    /**
     * Recipe ingredient class.
     * Author: Noelia Sales <noelia.salesmontes@gmail.com
     *
     * Class to contain all recipe ingredient's data. Needed from recipe class.
     * @implements Parcelable. Needed to pass this class through intents and bundles.
     */
    public class Ingredient implements Parcelable {

        // Main variables
        private Integer mSortNumber;
        private String mName;
        private Float mQuantity;
        private String mMeasurementUnit;


        // Constructors

        /**
         * Basic ingredient constructor
         * @param sortNumber Ingredient sort number
         * @param name Ingredient name
         * @param quantity Ingredient quantity
         * @param measurementUnit Ingredient measurement unit
         */
        public Ingredient(Integer sortNumber, String name, Float quantity,
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
        public Ingredient(JSONObject o) {
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
        public Ingredient(Parcel source){
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
        public final Parcelable.Creator CREATOR = new Parcelable.Creator<Ingredient>() {
            public Ingredient createFromParcel(Parcel source) {
                return new Ingredient(source);
            }

            public Ingredient[] newArray(int size) {
                return new Ingredient[size];
            }
        };

    }

    /**
     * Recipe direction class.
     * Author: Noelia Sales <noelia.salesmontes@gmail.com
     *
     * Class to contain all recipe directions's data. Needed from recipe class.
     * @implements Parcelable. Needed to pass this class through intents and bundles.
     */
    public class Direction implements Parcelable {

        // Main variables
        private Integer mSortNumber;
        private String mDescription;
        private String mImage;
        private String mVideo;
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
        public Direction(Integer sortNumber, String description, String image, String video,
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
        public Direction(JSONObject o) {
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
        public Direction(Parcel source){
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
        public final Parcelable.Creator CREATOR = new Parcelable.Creator<Direction>() {
            public Direction createFromParcel(Parcel source) {
                return new Direction(source);
            }

            public Direction[] newArray(int size) {
                return new Direction[size];
            }
        };
    }

}
