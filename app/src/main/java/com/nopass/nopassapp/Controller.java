package com.nopass.nopassapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.nopass.nopassapp.model.AskChallenge;
import com.nopass.nopassapp.model.Verif;

import java.security.PrivateKey;
import java.security.PublicKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hazegard on 26/03/18.
 */

public class Controller {
  private CypherHelper cypherHelper;
  private Context context;
  private onResponse callback;
  private ApiController apiController = new ApiController();

  public Controller(CypherHelper cypherHelper, Context context, onResponse callback) {
    this.cypherHelper = cypherHelper;
    this.context = context;
    this.callback = callback;
  }

  public void register(String username) {
    try {
      cypherHelper.createNewKey(username);
      String publicKey = cypherHelper.getPubKeyPem(username);
      apiController.register(username, publicKey, onRegister(username));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private Callback onRegister(final String username) {
    Callback onRegister = new Callback() {
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
        if (response.code() == 201) {
          Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
          Controller.this.resolveChallenge(username);
        } else {
          Controller.this.resolveChallenge(username);
//        try {
//          Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
        }
      }

      @Override
      public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        t.printStackTrace();
      }

    };
    return onRegister;
  }

  public void resolveChallenge(String username) {
    apiController.askChallenge(username, onAskChallenge(username));
  }

  private Callback onAskChallenge(final String username) {
    return new Callback<AskChallenge>() {
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
        AskChallenge askChallenge = (AskChallenge) response.body();
        try {
          PrivateKey privateKey = cypherHelper.getPrivKey(username);
          String pubkeyServer = askChallenge != null ? askChallenge.key : "";
          PublicKey publicKeyServer = cypherHelper.createPublicKey(pubkeyServer);
          String challenge = cypherHelper.decryptString(askChallenge != null ?
            askChallenge.encryptedChallenge : "", privateKey);
          String encryptedChallenge = cypherHelper.encryptString(challenge, publicKeyServer);
//          Thread.sleep(10000);
          apiController.verifyChallenge(username, encryptedChallenge, onVerifyChallenge);
        } catch (Exception e) {
          callback.onFailure();
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        callback.onFailure();

      }
    };
  }


  private Callback<Verif> onVerifyChallenge = new Callback<Verif>() {
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
      Verif verif = (Verif) response.body();
      Toast.makeText(context, verif != null ? verif.getRes().toString() : "Error", Toast.LENGTH_SHORT).show();
      if (verif.getRes()) {
        callback.onSuccess();
      } else {
        callback.onFailure();
      }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
      callback.onFailure();
      t.printStackTrace();
    }
  };
}
