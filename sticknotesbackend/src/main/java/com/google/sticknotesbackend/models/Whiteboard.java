package com.google.sticknotesbackend.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Whiteboard {
  @Id Long id;
  Long creationDate; //it's a timestamp
  @Load Ref<User> creator;
  String title;
  int rows;
  int cols;
  String backgroundImg;

  public Whiteboard() {}

  public Whiteboard(String title) {
    this.title = title;
  }

  public User getCreator(){
    return creator.get();
  }

  public void setCreator(User user){
    this.creator = Ref.create(user);
  }
}
