/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

public class BggGameCorrelation {
  private BggGame gameA;
  private BggGame gameB;
  private float correlation;
  private int commonRatings;
  
  public BggGameCorrelation(BggGame aGame, BggGame anotherGame, float correlation, int commonRatings) {
    if (aGame.getId() < 0 || anotherGame.getId() < 0) {
      throw new RuntimeException("Both games must be persisted");
    }
    if (aGame.getId() < anotherGame.getId()) {
      gameA = aGame;
      gameB = anotherGame;
    }
    else {
      gameA = anotherGame;
      gameB = aGame;
    }
    this.correlation = correlation;
    this.commonRatings = commonRatings;
  }

  public BggGame getGameA() {
    return gameA;
  }

  public void setGameA(BggGame gameA) {
    this.gameA = gameA;
  }

  public BggGame getGameB() {
    return gameB;
  }

  public void setGameB(BggGame gameB) {
    this.gameB = gameB;
  }

  public float getCorrelation() {
    return correlation;
  }

  public void setCorrelation(float correlation) {
    this.correlation = correlation;
  }

  public int getCommonRatings() {
    return commonRatings;
  }

  public void setCommonRatings(int commonRatings) {
    this.commonRatings = commonRatings;
  }   
}
