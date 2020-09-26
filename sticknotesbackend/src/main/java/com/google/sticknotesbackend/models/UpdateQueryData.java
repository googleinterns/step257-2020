/*
* Copyright 2020 Google LLC
*/
package com.google.sticknotesbackend.models;

public class UpdateQueryData {
  public Long id;
  public Long lastUpdated;

  public UpdateQueryData(Long id, Long lastUpdated){
    this.id = id;
    this.lastUpdated = lastUpdated;
  }
}
