package com.google.sticknotesbackend.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class UpdateQueryData {
  public Long id;
  public Long lastUpdated;

  public UpdateQueryData(){}

  public UpdateQueryData(Long id, Long lastUpdated){
    this.id = id;
    this.lastUpdated = lastUpdated;
  }
}
