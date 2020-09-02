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

  public boolean wasUpdated(){
    Note noteFromDatastore = ofy().load().type(Note.class).id(this.id).now();
    if(noteFromDatastore != null)
      return noteFromDatastore.lastUpdated != this.lastUpdated;
    return false;
  }
}
