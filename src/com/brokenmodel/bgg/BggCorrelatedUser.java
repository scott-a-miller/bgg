/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.text.DecimalFormat;

public class BggCorrelatedUser {
  private DecimalFormat correlationFormat = new DecimalFormat("0.000");
  private BggUser user;
  private float correlation;
  private int commonRatings;
  private float tanimoto;
  
  public BggCorrelatedUser(BggUser user, float correlation, int commonRatings, float tanimoto) {
    super();
    this.user = user;
    this.correlation = correlation;
    this.commonRatings = commonRatings;
    this.tanimoto = tanimoto;
  }
  
  public BggUser getUser() {
    return user;
  }
  public void setUser(BggUser user) {
    this.user = user;
  }
  public float getCorrelation() {
    return correlation;
  }
  public void setCorrelation(float correlation) {
    this.correlation = correlation;
  }
  public float getTanimoto() {
    return tanimoto;
  }
  public void setTanimoto(float tanimoto) {
    this.tanimoto = tanimoto;
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
  
  public String getFormattedTanimoto() {
    if (Float.isNaN(tanimoto)) {
      return null;
    }
    else {
      return correlationFormat.format(tanimoto);
    }
  }
}
