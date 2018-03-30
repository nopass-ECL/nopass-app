package com.nopass.nopassapp;

import com.nopass.nopassapp.model.AskChallenge;
import com.nopass.nopassapp.model.ChallengeWrapper;
import com.nopass.nopassapp.model.Res;
import com.nopass.nopassapp.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hazegard on 22/03/18.
 */

public interface NopassService {

  @GET("/api/auth/generateChallenge")
  Call<AskChallenge> askChallenge(@Query("username") String username);

  @POST("/api/resources/users/")
  Call<User> createUser(@Body User user);

  @POST("/api/auth/verifychallenge")
  Call<Res> verifyChallenge(@Query("username") String username, @Body ChallengeWrapper challenge);

  @GET("/api/resources/users/{user}")
  Call<Res> isUserexists(@Path("user") String username);
}
