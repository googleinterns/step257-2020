package com.google.sticknotesbackend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class User {
  public @Id String key; // String type to prevent autogeneration
  public String nickname;
  public @Index String email;

  // key is passed here because we are going to obtain it from UsersApi
  public User(String key, String nickname, String email) {
    this.key = key;
    this.nickname = nickname;
    this.email = email;
  }

  public User() {
  }
}
