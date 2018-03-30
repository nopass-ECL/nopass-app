package com.nopass.nopassapp.model;

/**
 * Created by hazegard on 22/03/18.
 */

public class User {
  private String name;
  private String publicKey;

  public User(String name, String publicKey) {
    this.name = name;
    this.publicKey = publicKey;
  }
}
