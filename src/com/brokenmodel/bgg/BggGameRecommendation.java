/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

public class BggGameRecommendation {
  private BggUser user;
  private BggCorrelatedUser recommendingUser;
  private int gameId;
  private float rating;
  
  public BggGameRecommendation(BggUser user, BggCorrelatedUser recommendingUser, int gameId, float rating) {
    super();
    this.user = user;
    this.recommendingUser = recommendingUser;
    this.gameId = gameId;
    this.rating = rating;
  }

  public BggUser getUser() {
    return user;
  }

  public void setUser(BggUser user) {
    this.user = user;
  }


  public BggCorrelatedUser getRecommendingUser() {
    return recommendingUser;
  }

  public void setRecommendingUser(BggCorrelatedUser recommendingUser) {
    this.recommendingUser = recommendingUser;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public float getRating() {
    return rating;
  }

  public void setRating(float rating) {
    this.rating = rating;
  }  
}
