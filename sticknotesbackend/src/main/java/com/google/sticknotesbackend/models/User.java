package com.google.sticknotesbackend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class User {
  public @Id Long id;
  public @Index String googleAccId; 
  public String nickname;
  public @Index String email;

  public User(String email, String nickname) {
    this.nickname = nickname;
    this.email = email;
  }

  public User() {
  }
}
