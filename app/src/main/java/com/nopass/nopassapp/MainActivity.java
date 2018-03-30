package com.nopass.nopassapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.text.Normalizer;


/**
 * Created by hazegard on 22/03/20.
 */

public class MainActivity extends AppCompatActivity implements onResponse, ApiController.OnConnectionTimeoutListener {
  public static final int VIEW_CONNEXION = 0;
  public static final int VIEW_SUCCESS = 1;
  public static final int VIEW_FAIL = 2;
  private Controller controller;
  ProgressBar progressBar;

  LinearLayout linearLayout;

  @Override
  public void onSuccess() {
    viewAnimator.setDisplayedChild(VIEW_SUCCESS);
    createSnackbar(R.string.connexion_successful, R.color.lightGreen);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
  }

  @Override
  public void onCancel() {
    progressBar.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onFailure() {
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    viewAnimator.setDisplayedChild(VIEW_FAIL);
    createSnackbar(R.string.fail_connexion, R.color.lightRed);
  }

  CypherHelper cypherHelper;
  ViewAnimator viewAnimator;
  EditText username;

  @Override
  public void onBackPressed() {
    progressBar.setVisibility(View.INVISIBLE);
    if (viewAnimator.getDisplayedChild() != VIEW_CONNEXION) {
      viewAnimator.setDisplayedChild(VIEW_CONNEXION);
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.appicon);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return false;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    linearLayout = findViewById(R.id.layout);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.appicon);
    viewAnimator = findViewById(R.id.annimator);
    username = findViewById(R.id.username);
    progressBar = findViewById(R.id.progressbar);
    try {
      cypherHelper = new CypherHelper();
      cypherHelper.createNewKey("Test20");
    } catch (Exception e) {
      e.printStackTrace();
    }
    controller = new Controller(cypherHelper, MainActivity.this, this, this);

    Button go = findViewById(R.id.go);
    go.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        String name = Normalizer.normalize(username.getText().toString()
          .replace(" ", ""), Normalizer.Form.NFD)
          .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
          .toLowerCase();
        controller.checkUser(name);
      }
    });
  }

  private void createSnackbar(int text, int color) {
    try {
      Snackbar mySnackbar = Snackbar.make(findViewById(R.id.layout),
        getString(text), Snackbar.LENGTH_SHORT);
      ((TextView) mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
        .setTextColor(color);
      TextView tv = mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
      tv.setTextColor(getColor(color));
      mySnackbar.show();
    } catch (Exception e) {
      //
    }
  }

  @Override
  public void onConnectionTimeout() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        createSnackbar(R.string.server_unreachable, R.color.colorPrimaryButton);
        progressBar.setVisibility(View.INVISIBLE);
      }
    });
  }


}
