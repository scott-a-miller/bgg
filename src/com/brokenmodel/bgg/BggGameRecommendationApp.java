/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggGameRecommendationApp {
  private static int minCommonRatings = 25;
  private static float requiredCorrelation = 0.5f;
  private static int topUserCount = 40;
  
  public static void main(String[] args) {
    try {
      MysqlBggRepository rep = new MysqlBggRepository();
      try {
        List<BggUser> users = rep.getCorrelatedUsers();
        int count = 1;
        for (BggUser user : users) {
          if (user.getName().compareTo("cradleofmilk") < 0) {
            break;
          }
          List<BggUserCorrelation> correlations = rep.getUserCorrelations(user, true);
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
          if (topUsers.size() > 0) {
            BggUserRatingList myList = rep.getUserRatingList(user);
            for (BggCorrelatedUser otherUser : topUsers) {
              BggUserRatingList otherList = rep.getUserRatingList(otherUser.getUser());
              BggUserRatingList diffList = otherList.removeCommonRatings(myList);
              int[] gameIds = diffList.getGameIds();
              float[] ratings = diffList.getRatings();
              for (int i = 0; i < gameIds.length; i++) {
                BggGameRecommendation recommendation = new BggGameRecommendation(user, otherUser, gameIds[i], ratings[i]);
                rep.addGameRecommendation(recommendation);
              }
            }            
          }
          System.out.println("Processed user " + user.getName() + " (" + minCorrelation + "): " + (count++) + " out of " + users.size() + "...");
        }
      }
      finally {
        try {rep.close();} catch (Exception e) { }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
