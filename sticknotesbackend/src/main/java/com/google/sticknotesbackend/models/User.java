/*
* Copyright 2020 Google LLC
*/
package com.google.sticknotesbackend.models;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class User implements Serializable {
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
