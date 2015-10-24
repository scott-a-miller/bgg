/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggGameRatingList {
  private BggGame game;
  private float[] ratings;
  private int[] userIds;

  public BggGameRatingList(BggGame game, List<Integer> userIds, List<Float> ratings) {
    this.game = game;
    this.ratings = new float[ratings.size()];
    this.userIds = new int[userIds.size()];
    if (this.ratings.length != this.userIds.length) {
      throw new RuntimeException("Ids and user ids are not parallel");
    }
    for (int i = 0; i < this.ratings.length; i++) {
      this.ratings[i] = ratings.get(i);
      this.userIds[i] = userIds.get(i);
    }
  }

  public BggGameRatingList(BggGame game, int[] userIds, float[] ratings) {
    this.game = game;
    this.ratings = ratings;
    this.userIds = userIds;
    if (this.ratings.length != this.userIds.length) {
      throw new RuntimeException("Ids and user ids are not parallel");
    }
  }

  public BggGame getGame() {
    return game;
  }

  public float[] getRatings() {
    return ratings;
  }
  
  public int[] userIds() {
    return userIds;
  }
  
  public BggGameCorrelation calculateCorrelation(BggGameRatingList other) {
    int myIdx = 0;
    int otherIdx = 0;
    List<Float> myCommonFloats = new ArrayList<Float>();
    List<Float> otherCommonFloats = new ArrayList<Float>();
      
    while (myIdx < ratings.length && otherIdx < other.ratings.length) {
      if (userIds[myIdx] == other.userIds[otherIdx]) {
        myCommonFloats.add(ratings[myIdx++]);
        otherCommonFloats.add(other.ratings[otherIdx++]);
      }
      else if (userIds[myIdx] < other.userIds[otherIdx]) {
        myIdx++;
      }
      else {
        otherIdx++;
      }
    }
    
    int n = myCommonFloats.size();
    float correlation = Float.NaN;
    if (n > 0) {
      float sumX = 0;
      float sumY = 0;
      float sumXY = 0;
      float sumXSquared = 0;
      float sumYSquared = 0;
      for (int i = 0; i < n; i++) { 
        float x = myCommonFloats.get(i);
        float y = otherCommonFloats.get(i);
        sumX += x;
        sumY += y;
        sumXY += x * y;
        sumXSquared += x * x;
        sumYSquared += y * y;
      }
      correlation = (float)(n * sumXY - sumX * sumY) / 
        ((float)Math.sqrt(n * sumXSquared - sumX * sumX) * 
         (float)Math.sqrt(n * sumYSquared - sumY * sumY));
      if (Float.isInfinite(correlation)) {
        correlation = Float.NaN;
      }
    }
    return new BggGameCorrelation(game, other.game, correlation, n);
  }
}
