/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.text.DecimalFormat;

public class BggCorrelatedGame {
  private DecimalFormat correlationFormat = new DecimalFormat("0.000");
  
  private BggGame game;
  private float correlation;
  private int commonRatings;
  
  public BggCorrelatedGame(BggGame game, float correlation, int commonRatings) {
    super();
    this.game = game;
    this.correlation = correlation;
    this.commonRatings = commonRatings;
  }
  
  public BggGame getGame() {
    return game;
  }
  public void setGame(BggGame game) {
    this.game = game;
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

  public String getFormattedCorrelation() {
    if (Float.isNaN(correlation)) {
      return null;
    }
    else {
      return correlationFormat.format(correlation);
    }
  }
}
