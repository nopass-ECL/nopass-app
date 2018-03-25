package com.nopass.nopassapp;

import com.nopass.nopassapp.model.AskChallenge;
import com.nopass.nopassapp.model.ChallengeWrapper;
import com.nopass.nopassapp.model.User;
import com.nopass.nopassapp.model.Verif;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hazegard on 22/03/18.
 */

public class Controller {
  private final static String URL = "http://10.0.3.2:4000";

  private Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build();

  private NopassService service = retrofit.create(NopassService.class);

  void askChallenge(String username, Callback cb) {
    Call<AskChallenge> resp = service.askChallenge(username);
    resp.enqueue(cb);
  }

  public void register(String username, String pukKey, Callback callback) {
    User user = new User(username, pukKey);
    Call<User> resp = service.createUser(user);
    resp.enqueue(callback);
  }

  void verifyChallenge(String username, String encryptedChallenge, Callback<Verif> cb) {
    ChallengeWrapper challengeWrapper = new ChallengeWrapper(encryptedChallenge);
    Call<Verif> resp = service.verifyChallenge(username, challengeWrapper);
    resp.enqueue(cb);
  }
}
