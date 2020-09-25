package com.google.sticknotesbackend.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

//this is not datastore entity, it will only be stored in cache
public class ActiveUsers implements Serializable {
  public Long boardId;

  //this is a list of users id and last activity timestamp for each of them 
  public List<Pair<Long, Long>> usersLastActivity;

  public ActiveUsers(Long boardId){
    this.boardId = boardId;
    this.usersLastActivity = new ArrayList<>();
  }
}
