/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.io.*;
import java.util.*;

public class BggGameRecommender {
  private static BggGameRecommender instance;  
  public static synchronized BggGameRecommender getInstance() throws Exception {
    if (instance == null) {
      instance = new BggGameRecommender();
    }
    return instance;
  }
 
  private MysqlBggRepository rep;
  private int minCommonRatings = 25;
  private float requiredCorrelation = 0.4f;
  private int topUserCount = 40;

  private BggGameRecommender() throws Exception {
    rep = new MysqlBggRepository();
  }
  
  public BggGameRecommender(MysqlBggRepository rep) {
    this.rep = rep;
  }
  
  public BggUser getUser(String name) throws Exception {
    return rep.getUser(name);
  }
    
  public List<BggAvgRecommendation> getAvgRecommendations(BggUser user, boolean orderByTanimoto) throws Exception {
    List<BggAvgRecommendation> avgRecs = BggAvgRecommendation.createAvgRecommendations(user, getRecommendations(user, orderByTanimoto), rep, orderByTanimoto);
    List<BggAvgRecommendation> filteredAvgRecs = new ArrayList<BggAvgRecommendation>();
    for (BggAvgRecommendation avgRec : avgRecs) {
      if (avgRec.getAvgRating() >= 7f) {
        filteredAvgRecs.add(avgRec);
      }
    }
    BggAvgRecommendation.sortByWeight(filteredAvgRecs);    
    return filteredAvgRecs;
  }
  
  public List<BggGameRecommendation> getRecommendations(BggUser user, boolean orderByTanimoto) throws Exception {
    List<BggUserCorrelation> correlations = rep.getUserCorrelations(user, orderByTanimoto);
    List<BggCorrelatedUser> topUsers = new ArrayList<BggCorrelatedUser>(topUserCount);
    float minCorrelation = 1f;
    for (BggUserCorrelation correlation : correlations) {
      if (correlation.getCommonRatings() >= minCommonRatings && correlation.getCorrelation() >= requiredCorrelation) {
        if (correlation.getUserA().getId() != user.getId()) {
          topUsers.add(new BggCorrelatedUser(correlation.getUserA(), correlation.getCorrelation(), correlation.getCommonRatings(), correlation.getTanimoto()));
        }
        else {
          topUsers.add(new BggCorrelatedUser(correlation.getUserB(), correlation.getCorrelation(), correlation.getCommonRatings(), correlation.getTanimoto()));
        }
        if (minCorrelation > correlation.getCorrelation()) {
          minCorrelation = correlation.getCorrelation();
        }
      }
      if (topUsers.size() >= topUserCount) {
        break;
      }
    }
    
    List<BggGameRecommendation> recommendations = new ArrayList<BggGameRecommendation>();
    if (topUsers.size() > 0) {
      BggUserRatingList myList = rep.getUserRatingList(user);
      for (BggCorrelatedUser otherUser : topUsers) {
        BggUserRatingList otherList = rep.getUserRatingList(otherUser.getUser());
        BggUserRatingList diffList = otherList.removeCommonRatings(myList);
        int[] gameIds = diffList.getGameIds();
        float[] ratings = diffList.getRatings();
        for (int i = 0; i < gameIds.length; i++) {
          BggGameRecommendation recommendation = new BggGameRecommendation(user, otherUser, gameIds[i], ratings[i]);
          recommendations.add(recommendation);
        }
      }            
    }    
    
    return recommendations;
  }
  
  public void close() throws Exception {
    rep.close();
  }
  
  public static void main(String[] args) {
    try {
      BggGameRecommender recommender = BggGameRecommender.getInstance();
      BufferedWriter bw = null;
      try {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
          bw = new BufferedWriter(new PrintWriter(System.out));
          for (BggAvgRecommendation avgRec : recommender.getAvgRecommendations(recommender.getUser("CitizenBob"), true)) {
            bw.write(avgRec.getGame().getName());
            bw.write("\t");
            bw.write(String.valueOf(avgRec.getAvgRating()));
            bw.write("\t");
            bw.write(String.valueOf(avgRec.getWeightedRating()));
            bw.write("\t");
            bw.write(String.valueOf(avgRec.getNumRatings()));
            bw.write("\t");
            for (BggGameRecommendation rec : avgRec.getRecommendations()) {
              bw.write(rec.getRecommendingUser().getUser().getName());
              bw.write(" : ");
              bw.write(String.valueOf(rec.getRating()));
              bw.write(" : ");
              bw.write(String.valueOf(rec.getRecommendingUser().getCorrelation()));
              bw.write(" : ");
              bw.write(String.valueOf(rec.getRecommendingUser().getCommonRatings()));
              bw.write("\t");
            }
            bw.newLine();          
          }
        }
        bw.write(((System.currentTimeMillis() - startTime) / (float)1000) + " secs");
        bw.newLine();
      }
      finally {
        try { recommender.close(); } catch (Exception e) { }
        try { if (bw != null) { bw.close(); } } catch (Exception e) { }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
