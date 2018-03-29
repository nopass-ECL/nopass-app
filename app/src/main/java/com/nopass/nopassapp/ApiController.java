package com.nopass.nopassapp;

import android.support.annotation.NonNull;

import com.nopass.nopassapp.model.AskChallenge;
import com.nopass.nopassapp.model.ChallengeWrapper;
import com.nopass.nopassapp.model.Res;
import com.nopass.nopassapp.model.User;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hazegard on 22/03/18.
 */

public class ApiController {
  public ApiController(OnConnectionTimeoutListener listener) {
    this.listener = listener;
  }

  private OnConnectionTimeoutListener listener;
  //  private final static String URL = "http://10.0.3.2:4000";
  private final static String URL = "http://nopass.hazegard.fr";

  private OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .addInterceptor(new Interceptor() {
      @Override
      public Response intercept(@NonNull Chain chain) throws IOException {
        return onOnIntercept(chain);
      }
    })
    .build();

  private Response onOnIntercept(Interceptor.Chain chain) throws IOException {
    try {
      return chain.proceed(chain.request());
    } catch (Exception exception) {
      exception.printStackTrace();
      if (listener != null)
        listener.onConnectionTimeout();
    }

    return chain.proceed(chain.request());
  }

  private Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build();

  private NopassService service = retrofit.create(NopassService.class);

  void askChallenge(String username, Callback<AskChallenge> cb) {
    Call<AskChallenge> resp = service.askChallenge(username);
    resp.enqueue(cb);
  }

  public void register(String username, String pukKey, Callback<User> callback) {
    User user = new User(username, pukKey);
    Call<User> resp = service.createUser(user);
    resp.enqueue(callback);
  }

  void verifyChallenge(String username, String encryptedChallenge, Callback<Res> cb) {
    ChallengeWrapper challengeWrapper = new ChallengeWrapper(encryptedChallenge);
    Call<Res> resp = service.verifyChallenge(username, challengeWrapper);
    resp.enqueue(cb);
  }

  void isUserexists(String username, Callback<Res> cb) {
    Call<Res> resp = service.isUserexists(username);
    resp.enqueue(cb);
  }

  public interface OnConnectionTimeoutListener {
    void onConnectionTimeout();
  }
}
