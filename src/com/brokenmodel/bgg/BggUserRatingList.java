/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggUserRatingList {
  private BggUser user;
  private float[] ratings;
  private int[] gameIds;
  private int[] superlativeGameIds;

  public BggUserRatingList(BggUser user, List<Integer> gameIds, List<Float> ratings) {
    this.user = user;
    this.ratings = new float[ratings.size()];
    this.gameIds = new int[gameIds.size()];
    if (this.ratings.length != this.gameIds.length) {
      throw new RuntimeException("Ids and game ids are not parallel");
    }
    List<Integer> superlativeGameIdList = new ArrayList<Integer>();
    for (int i = 0; i < this.ratings.length; i++) {
      this.ratings[i] = ratings.get(i);
      this.gameIds[i] = gameIds.get(i);
      if (this.ratings[i] >= user.getSuperlativeThreshold()) {
        superlativeGameIdList.add(this.gameIds[i]);
      }
    }    
    superlativeGameIds = new int[superlativeGameIdList.size()];
    for (int i = 0; i < superlativeGameIds.length; i++) {
      superlativeGameIds[i] = superlativeGameIdList.get(i);
    }
  }

  public BggUserRatingList(BggUser user, int[] gameIds, float[] ratings) {
    this.user = user;
    this.ratings = ratings;
    this.gameIds = gameIds;
    if (this.ratings.length != this.gameIds.length) {
      throw new RuntimeException("Ids and game ids are not parallel");
    }
  }

  public BggUser getUser() {
    return user;
  }

  public float[] getRatings() {
    return ratings;
  }
  
  public int[] getGameIds() {
    return gameIds;
  }
  
  public BggUserRatingList removeCommonRatings(BggUserRatingList other) {
    List<Integer> filteredGameIds = new ArrayList<Integer>();
    List<Float> filteredRatings = new ArrayList<Float>();
    int myIdx = 0;
    int otherIdx = 0;

    while (myIdx < ratings.length && otherIdx < other.ratings.length) {
      if (gameIds[myIdx] == other.gameIds[otherIdx]) {
        myIdx++; otherIdx++;
      }
      else if (gameIds[myIdx] < other.gameIds[otherIdx]) {
        filteredGameIds.add(gameIds[myIdx]);
        filteredRatings.add(ratings[myIdx]);
        myIdx++;
      }
      else {
        otherIdx++;
      }
    }
    return new BggUserRatingList(user, filteredGameIds, filteredRatings);
  }
  
  public BggUserCorrelation calculateCorrelation(BggUserRatingList other) {
    int myIdx = 0;
    int otherIdx = 0;
    List<Float> myCommonFloats = new ArrayList<Float>();
    List<Float> otherCommonFloats = new ArrayList<Float>();
      
    while (myIdx < ratings.length && otherIdx < other.ratings.length) {
      if (gameIds[myIdx] == other.gameIds[otherIdx]) {
        myCommonFloats.add(ratings[myIdx++]);
        otherCommonFloats.add(other.ratings[otherIdx++]);
      }
      else if (gameIds[myIdx] < other.gameIds[otherIdx]) {
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
        
    float tanimoto = computeTanimoto(superlativeGameIds, other.superlativeGameIds);
    
    return new BggUserCorrelation(user, other.user, correlation, n, tanimoto);
  }
  
  private float computeTanimoto(int[] gameIds1, int[] gameIds2) {
    int length1 = gameIds1.length;
    int length2 = gameIds2.length;
    
    if (length1 == 0 || length2 == 0) {
      return 0f;
    }
    
    int commonPop = 0;
    int unionPop = length1 + length2;
    int idx1 = 0;
    int idx2 = 0;
    while (idx1 < length1 && idx2 < length2) {
      int f1 = gameIds1[idx1];
      int f2 = gameIds2[idx2];
      if (f1 == f2) {
        commonPop++;
        idx1++;
        idx2++;
      }
      else if (f1 < f2) {
        idx1++;
      }
      else {
        idx2++;
      }
    }
    
    return commonPop / (float)(unionPop - commonPop);
  }
}
