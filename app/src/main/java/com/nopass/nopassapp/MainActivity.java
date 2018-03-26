package com.nopass.nopassapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewAnimator;


/**
 * Created by hazegard on 22/03/20.
 */

public class MainActivity extends AppCompatActivity implements onResponse {
  public static final int VIEW_CONNEXION = 0;
  public static final int VIEW_SUCCESS = 1;
  public static final int VIEW_FAIL = 2;
  private Controller controller;
  private Toolbar toolbar;

  @Override
  public void onSuccess() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    viewAnimator.setDisplayedChild(VIEW_SUCCESS);
  }

  @Override
  public void onFailure() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    viewAnimator.setDisplayedChild(VIEW_FAIL);
  }

  CypherHelper cypherHelper;
  ViewAnimator viewAnimator;
  EditText username;

  @Override
  public void onBackPressed() {
    if(viewAnimator.getDisplayedChild() == VIEW_CONNEXION){

    } else {
      viewAnimator.setDisplayedChild(VIEW_CONNEXION);
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    viewAnimator = findViewById(R.id.annimator);
    username = findViewById(R.id.username);


    try {
      cypherHelper = new CypherHelper();
      cypherHelper.createNewKey("Test20");
    } catch (Exception e) {
      e.printStackTrace();
    }
    controller = new Controller(cypherHelper, MainActivity.this, this);

    Button go = findViewById(R.id.go);
    go.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String un = username.getText().toString();
        controller.register(username.getText().toString());
      }
    });


//    Button askChallenge = findViewById(R.id.askChallenge);
//    Button getKey = findViewById(R.id.getKey);
//    Button register = findViewById(R.id.register);
//    askChallenge.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(final View v) {
//        apiController.askChallenge("Maxime_Chiffay20", new Callback() {
//          @Override
//          public void onFailure(@NonNull Call call, @NonNull Throwable t) {
//            t.printStackTrace();
//            viewAnimator.setDisplayedChild(VIEW_FAIL);
//          }
//
//          @Override
//          public void onResponse(@NonNull Call call, @NonNull Response response) {
//            AskChallenge askChallenge = (AskChallenge) response.body();
//            Log.d("Controller", askChallenge.encryptedChallenge + "\n" + askChallenge.key);
//            try {
//              String value = cypherHelper.decryptString(askChallenge.encryptedChallenge, cypherHelper.getPrivKey("Test20"));
//              Log.d("Decrypted", value);
//              PublicKey key = cypherHelper.createPublicKey(askChallenge.key);
//              String test = cypherHelper.encryptString(value, key);
//              Log.d("Encrypted", test.replace("\n", ""));
//              apiController.verifyChallenge("Maxime_Chiffay20", test, verif);
//            } catch (Exception e) {
//              e.printStackTrace();
//              viewAnimator.setDisplayedChild(VIEW_FAIL);
//            }
//          }
//        });
//      }
//    });
//    getKey.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//
//        try {
//
//          Log.d("Pub", cypherHelper.getPubKeyPem("Test20"));
//        } catch (Exception e) {
//          e.printStackTrace();
//          viewAnimator.setDisplayedChild(VIEW_FAIL);
//        }
//      }
//    });

//    register.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        try {
//          String pubKey = cypherHelper.getPubKeyPem("Test20");
//          apiController.register("Maxime_Chiffay20", pubKey, new Callback() {
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) {
//
//              if (response.code() == 201) {
//                Log.d("request", response.message());
//              } else {
//                try {
//                  Log.d("request", response.code() + " : " + response.errorBody().string());
//                } catch (IOException e) {
//                  e.printStackTrace();
//                  viewAnimator.setDisplayedChild(VIEW_FAIL);
//                }
//              }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
//              t.printStackTrace();
//            }
//          });
//        } catch (Exception e) {
//          e.printStackTrace();
//          viewAnimator.setDisplayedChild(VIEW_FAIL);
//        }
//      }
//    });

  }

//  Callback verif = new Callback() {
//    @Override
//    public void onResponse(@NonNull Call call, @NonNull Response response) {
//      Verif v = (Verif) response.body();
//      Log.d("Verif", v.getRes().toString());
//      if (v.getRes()) {
//        viewAnimator.setDisplayedChild(VIEW_SUCCESS);
//      } else {
//        viewAnimator.setDisplayedChild(VIEW_FAIL);
//
//      }
//    }
//
//    @Override
//    public void onFailure(@NonNull Call call, @NonNull Throwable t) {
//      t.printStackTrace();
//      viewAnimator.setDisplayedChild(VIEW_FAIL);
//    }
//  };

}
