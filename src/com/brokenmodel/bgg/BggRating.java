/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

public class BggRating {
  private BggGame game;
  private BggUser user;
  private float rating = Float.NaN;
  private float bggRating = Float.NaN;
    
  public BggRating(BggUser user, BggGame game) {
    this.user = user;
    this.game = game;
  }

  public BggRating(BggUser user, BggGame game, float rating, float bggRating) {
    this.user = user;
    this.game = game;
    this.rating = rating;
    this.bggRating = bggRating;
  }
  
  public float getBggRating() {
    return bggRating;
  }

  public void setBggRating(float bggRating) {
    this.bggRating = bggRating;
  }

  public BggGame getGame() {
    return game;
  }

  public void setGame(BggGame game) {
    this.game = game;
  }

  public BggUser getUser() {
    return user;
  }

  public void setUser(BggUser user) {
    this.user = user;
  }

  public float getRating() {
    return rating;
  }

  public void setRating(float rating) {
    this.rating = rating;
  }
}
