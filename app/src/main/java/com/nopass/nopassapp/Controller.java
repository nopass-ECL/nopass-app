package com.nopass.nopassapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.widget.TextView;

import com.nopass.nopassapp.model.AskChallenge;
import com.nopass.nopassapp.model.Res;

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
  private Activity context;
  private onResponse callback;
  private ApiController apiController;

  public Controller(CypherHelper cypherHelper, Activity context, onResponse callback, ApiController.OnConnectionTimeoutListener listener) {
    this.cypherHelper = cypherHelper;
    this.context = context;
    this.callback = callback;
    this.apiController = new ApiController(listener);
  }

  public void checkUser(String username) {
    apiController.isUserexists(username, onCheckUser(username));
  }

  private Callback onCheckUser(final String username) {
    return new Callback() {
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
        Res res = (Res) response.body();
        if (res != null && (res.getRes() != null ? res.getRes() : false)) {
          Controller.this.resolveChallenge(username);
        } else {
          AlertDialog dialog = new AlertDialog.Builder(context)
            .setMessage(Html.fromHtml("Le compte <font color='#2365ff'>" + username + "</font> n'existe pas. <br>Créer une paire de clé et s'inscrire?"))
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Controller.this.register(username);
                createSnackbar(R.string.gen_keys, R.color.colorPrimaryButton);
              }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                createSnackbar(R.string.cancel_register, R.color.colorPrimaryButton);
                callback.onCancel();
              }
            })
            .create();
          dialog.show();
//          Controller.this.register(username);
        }
      }

      @Override
      public void onFailure(Call call, Throwable t) {
        t.printStackTrace();
      }
    };
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
    return new Callback() {
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
//        if (response.code() == 201) {
//          Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
        Controller.this.resolveChallenge(username);
//        } else {
//          Controller.this.resolveChallenge(username);
//        }
      }

      @Override
      public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        callback.onFailure();
        t.printStackTrace();
      }

    };
  }

  private void createSnackbar(int text, int color) {
    try {
      Snackbar mySnackbar = Snackbar.make(context.findViewById(R.id.layout),
        context.getString(text), Snackbar.LENGTH_LONG);
      ((TextView) mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
        .setTextColor(color);
      TextView tv = mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
      tv.setTextColor(context.getColor(color));
      mySnackbar.show();
    } catch (Exception e) {
      //
    }
  }

  public void resolveChallenge(String username) {
    apiController.askChallenge(username, onAskChallenge(username));
  }

  private Callback onAskChallenge(final String username) {
    return new Callback<AskChallenge>() {
      @Override
      public void onResponse(@NonNull Call call, @NonNull Response response) {
        AskChallenge askChallenge = (AskChallenge) response.body();
        PrivateKey privateKey = cypherHelper.getPrivKey(username);
        String pubKeyServer = askChallenge != null ? askChallenge.key : "";
        PublicKey publicKeyServer = cypherHelper.createPublicKey(pubKeyServer);
        String challenge = cypherHelper.decryptString(askChallenge != null ?
          askChallenge.encryptedChallenge : "", privateKey);
        String encryptedChallenge = cypherHelper.encryptString(challenge, publicKeyServer);
        apiController.verifyChallenge(username, encryptedChallenge, onVerifyChallenge);
      }

      @Override
      public void onFailure(@NonNull Call call, @NonNull Throwable t) {
        callback.onFailure();
      }
    };
  }

  private Callback<Res> onVerifyChallenge = new Callback<Res>() {
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
      Res verif = (Res) response.body();
//      Toast.makeText(context, verif != null ? verif.getRes().toString() : "Error", Toast.LENGTH_SHORT).show();
      if (verif != null ? verif.getRes() : false) {
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
