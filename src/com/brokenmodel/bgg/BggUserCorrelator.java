/**
 * Copyright 2009 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.bgg;

import java.util.*;

public class BggUserCorrelator {
  private static int topUserCount = 40;
  
  public static void main(String[] args) {
    
    try {
      MysqlBggRepository rep = new MysqlBggRepository();      
      try {      
        List<BggUser> users = rep.getMostRatedUsers(100);
        // List<BggUser> users = Arrays.asList(rep.getUser("Romain"), rep.getUser("CitizenBob"));
        
        List<BggUserRatingList> lists = new ArrayList<BggUserRatingList>(users.size());
        for (BggUser user : users) {
          lists.add(rep.getUserRatingList(user));
        }
        
        System.out.println("Starting calculations for " + users.size() + " users...");
        for (int i = 0; i < users.size(); i++) {          
          List<BggUserCorrelation> correlations = new ArrayList<BggUserCorrelation>();
          for (int j = 0; j < users.size(); j++) {
            if (j != i) {
              BggUserRatingList listB = lists.get(i);
              BggUserRatingList listA = lists.get(j);
              BggUserCorrelation newCorrelation = listA.calculateCorrelation(listB);
              if (!Float.isNaN(newCorrelation.getTanimoto()) && newCorrelation.getCommonRatings() >= 25) {
                correlations.add(newCorrelation);
              }
            }
          }
          Collections.sort(correlations, new Comparator<BggUserCorrelation>() {
            public int compare(BggUserCorrelation o1, BggUserCorrelation o2) {
              int correlationResult = Float.compare(o1.getTanimoto(), o2.getTanimoto());
              if (correlationResult != 0) {
                return -1 * correlationResult;
              }
              if (o1.getCommonRatings() > o2.getCommonRatings()) {
                return -1;
              }
              else if (o1.getCommonRatings() < o2.getCommonRatings()) {
                return 1;
              }
              return 0;
            }            
          });          
          for (int j = 0; j < topUserCount && j < correlations.size(); j++) {
            rep.addUserCorrelation(correlations.get(j));
          }
          Collections.sort(correlations, new Comparator<BggUserCorrelation>() {
            public int compare(BggUserCorrelation o1, BggUserCorrelation o2) {
              int correlationResult = Float.compare(o1.getCorrelation(), o2.getCorrelation());
              if (correlationResult != 0) {
                return -1 * correlationResult;
              }
              if (o1.getCommonRatings() > o2.getCommonRatings()) {
                return -1;
              }
              else if (o1.getCommonRatings() < o2.getCommonRatings()) {
                return 1;
              }
              return 0;
            }            
          });          
          for (int j = 0; j < topUserCount && j < correlations.size(); j++) {
            rep.addUserCorrelation(correlations.get(j));
          }
          if (i % 100 == 0) {
            System.out.println("Calculated for " + i + " users...");
          }
        }
        for (BggUser user : users) {
          rep.setUserAsCorrelated(user, true);
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
