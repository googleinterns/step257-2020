package com.google.sticknotesbackend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class User {
  public @Id Long key; 
  public String nickname;
  public @Index String email;

  public User(String nickname, String email) {
    this.nickname = nickname;
    this.email = email;
  }

  public User() {
  }
}
