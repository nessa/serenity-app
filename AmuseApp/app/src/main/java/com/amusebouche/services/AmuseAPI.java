package com.amusebouche.services;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * AmuseAPI interface.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Requests definitions used with retrofit.
 */
public interface AmuseAPI {

    // Authentication requests

    @GET("auth/me/")
    Call<ResponseBody> me();

    @FormUrlEncoded
    @POST("auth/login/")
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);

    @POST("auth/logout/")
    Call<ResponseBody> logout();

    @FormUrlEncoded
    @POST("auth/register/")
    Call<ResponseBody> register(@Field("username") String username, @Field("email") String email,
                                @Field("first_name") String name, @Field("last_name") String surname,
                                @Field("password") String password);


    // Recipes requests

    @GET
    Call<ResponseBody> getRecipes(@Url String url);

    @GET("recipes/{id}/")
    Call<ResponseBody> getRecipe(@Path("id") String id);

    @POST("recipes/")
    Call<ResponseBody> createRecipe(@Body JSONObject body);

    @PUT("recipes/{id}/")
    Call<ResponseBody> updateRecipe(@Path("id") String id, @Body JSONObject body);


    // Recipe's comments requests

    @GET("comments/")
    Call<ResponseBody> getComments(@Query("recipe") String id);

    @FormUrlEncoded
    @POST("comments/")
    Call<ResponseBody> addComment(@Field("recipe") String recipe, @Field("comment") String comment);


    // Recipe's ratings requests

    @FormUrlEncoded
    @POST("ratings/")
    Call<ResponseBody> rateRecipe(@Field("recipe") String recipe, @Field("rating") Integer rating);


    // Ingredients requests

    @GET("translations/")
    Call<ResponseBody> getIngredients(@Query("page") Integer page,
                                      @Query("language") String language,
                                      @Query("updated_after") String date);
}
