package com.nopass.nopassapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nopass.nopassapp.model.AskChallenge;
import com.nopass.nopassapp.model.Verif;

import java.io.IOException;
import java.security.PublicKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by hazegard on 22/03/19.
 */

public class MainActivity extends AppCompatActivity {
  private Controller controller = new Controller();
  CypherHelper cypherHelper;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    try {
      cypherHelper = new CypherHelper();
      cypherHelper.createNewKey("Test19");
    } catch (Exception e) {
      e.printStackTrace();
    }

    Button askChallenge = findViewById(R.id.askChallenge);
    Button getKey = findViewById(R.id.getKey);
    Button register = findViewById(R.id.register);
    askChallenge.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        controller.askChallenge("Maxime_Chiffay19", new Callback() {
          @Override
          public void onFailure(Call call, Throwable t) {
            ;
          }

          @Override
          public void onResponse(Call call, Response response) {
            AskChallenge askChallenge = (AskChallenge) response.body();
            Log.d("Controller", askChallenge.encryptedChallenge + "\n" + askChallenge.key);
            try {
              String value = cypherHelper.decryptString(askChallenge.encryptedChallenge, cypherHelper.getPrivKey("Test19"));
              Log.d("Decrypted", value);
              PublicKey key = cypherHelper.createPublicKey(askChallenge.key);
              String test = cypherHelper.encryptString(value, key);
              Log.d("Encrypted", test.replace("\n", ""));
              controller.verifyChallenge("Maxime_Chiffay19", test, verif);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
      }
    });
    getKey.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        try {

          Log.d("Pub", cypherHelper.getPubKeyPem("Test19"));
//          Log.d("Priv", cypherHelper.getPrivKey("Test").toString());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          String pubKey = cypherHelper.getPubKeyPem("Test19");
          controller.register("Maxime_Chiffay19", pubKey, new Callback() {

            @Override
            public void onResponse(Call call, Response response) {

              if (response.code() == 201) {
                Log.d("request", response.message());
              } else {
                try {
                  Log.d("request", response.code() + " : " + response.errorBody().string());
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
              t.printStackTrace();
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

  }

  Callback verif = new Callback() {
    @Override
    public void onResponse(Call call, Response response) {
      Verif v = (Verif) response.body();
      Log.d("Verif", v.getRes().toString());
    }

    @Override
    public void onFailure(Call call, Throwable t) {
      t.printStackTrace();
    }
  };
}
