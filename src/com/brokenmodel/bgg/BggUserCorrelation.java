/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

public class BggUserCorrelation {
  private BggUser userA;
  private BggUser userB;
  private float correlation;
  private int commonRatings;
  private float tanimoto;
  
  public BggUserCorrelation(BggUser aUser, BggUser anotherUser, float correlation, int commonRatings, float tanimoto) {
    if (aUser.getId() < 0 || anotherUser.getId() < 0) {
      throw new RuntimeException("Both users must be persisted");
    }
    if (aUser.getId() < anotherUser.getId()) {
      userA = aUser;
      userB = anotherUser;
    }
    else {
      userA = anotherUser;
      userB = aUser;
    }
    this.correlation = correlation;
    this.commonRatings = commonRatings;
    this.tanimoto = tanimoto;
  }

  public BggUser getUserA() {
    return userA;
  }

  public void setUserA(BggUser userA) {
    this.userA = userA;
  }

  public BggUser getUserB() {
    return userB;
  }

  public void setUserB(BggUser userB) {
    this.userB = userB;
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

  public float getTanimoto() {
    return tanimoto;
  }

  public void setTanimoto(float tanimoto) {
    this.tanimoto = tanimoto;
  }  
}
